package dev.skbindas.jev.modules

import dev.skbindas.jev.client.JevClient
import dev.skbindas.jev.client.JevTransport
import dev.skbindas.jev.core.Decision
import dev.skbindas.jev.core.JevResponse
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DecisionModulesTest {
    @Test
    fun entitled_users_are_suppressed_without_model_call() = runTest {
        var calls = 0
        val client = JevClient(
            JevTransport {
                calls += 1
                JevResponse()
            }
        )

        val result = PaywallDecider(client).decide(
            PaywallContext(10, 5, false, true)
        )

        assertEquals(PaywallAction.SUPPRESS, (result as Decision.Accepted).value)
        assertEquals(0, calls)
    }

    @Test
    fun paywall_unknown_choice_abstains() = runTest {
        val client = JevClient(
            JevTransport {
                JevResponse(
                    answers = mapOf(
                        "paywall_action" to buildJsonObject {
                            put("choice", "unexpected")
                            put("confidence", 0.99)
                        }
                    )
                )
            }
        )

        val result = PaywallDecider(client).decide(
            PaywallContext(3, 2, false, false)
        )

        assertIs<Decision.Abstained>(result)
        assertEquals("unexpected_jev_choice", result.reason)
    }

    @Test
    fun high_priority_notifications_bypass_semantic_suppression() = runTest {
        var calls = 0
        val client = JevClient(
            JevTransport {
                calls += 1
                JevResponse()
            }
        )

        val result = NotificationRouter(client).decide(
            NotificationContext("Security alert", "Important", false, 5)
        )

        assertEquals(NotificationAction.DELIVER, (result as Decision.Accepted).value)
        assertEquals(0, calls)
    }

    @Test
    fun notification_unknown_choice_abstains() = runTest {
        val client = JevClient(
            JevTransport {
                JevResponse(
                    answers = mapOf(
                        "notification_action" to buildJsonObject {
                            put("choice", "unexpected")
                            put("confidence", 0.99)
                        }
                    )
                )
            }
        )

        val result = NotificationRouter(client).decide(
            NotificationContext("Update", "Body", false, 1)
        )

        assertIs<Decision.Abstained>(result)
        assertEquals("unexpected_jev_choice", result.reason)
    }

    @Test
    fun reranker_batches_candidates_into_one_jev_call() = runTest {
        var calls = 0
        var questionCount = 0
        val client = JevClient(
            JevTransport { request ->
                calls += 1
                questionCount = request.questions.size
                JevResponse(
                    answers = request.questions.keys.associateWith { id ->
                        buildJsonObject {
                            put("score", if (id == "fit_1") 10 else 4)
                            put("confidence", 0.9)
                        }
                    }
                )
            }
        )

        val result = SemanticReranker(client).rerank(
            "android notifications",
            listOf(
                SearchCandidate("a", "Payments", "billing"),
                SearchCandidate("b", "Notifications", "push notification routing"),
                SearchCandidate("c", "Weather", "forecast")
            )
        )

        assertEquals(1, calls)
        assertEquals(3, questionCount)
        assertEquals("b", result.first().candidate.id)
        assertEquals(RankingStatus.RANKED, result.first().status)
    }

    @Test
    fun reranker_preserves_low_confidence_candidates_as_abstained() = runTest {
        val client = JevClient(
            JevTransport {
                JevResponse(
                    answers = mapOf(
                        "fit_0" to buildJsonObject {
                            put("score", 6)
                            put("confidence", 0.51)
                        }
                    )
                )
            }
        )

        val result = SemanticReranker(client).rerank(
            "ambiguous query",
            listOf(SearchCandidate("a", "Maybe", "uncertain match"))
        )

        assertEquals(RankingStatus.ABSTAINED, result.single().status)
        assertEquals(null, result.single().score)
        assertEquals("score_confidence_below_threshold", result.single().abstainReason)
    }

    @Test
    fun reranker_preserves_missing_answers_as_abstained_candidates() = runTest {
        val client = JevClient(JevTransport { JevResponse() })

        val result = SemanticReranker(client).rerank(
            "missing answer",
            listOf(SearchCandidate("a", "Maybe", "no model answer"))
        )

        assertEquals(1, result.size)
        assertEquals(RankingStatus.ABSTAINED, result.single().status)
        assertEquals("missing_jev_answer", result.single().abstainReason)
    }

    @Test
    fun moderation_supports_human_review() = runTest {
        val response = JevResponse(
            answers = mapOf(
                "moderation_action" to buildJsonObject {
                    put("choice", "review")
                    put("confidence", 0.93)
                    put("probabilities", buildJsonObject {
                        put("review", 0.93)
                        put("allow", 0.04)
                        put("reject", 0.03)
                    })
                }
            )
        )
        val client = JevClient(JevTransport { response })

        val result = ModerationDecider(client).decide(ModerationContext("borderline case"))

        assertIs<Decision.Accepted<ModerationAction>>(result)
        assertEquals(ModerationAction.REVIEW, result.value)
    }

    @Test
    fun moderation_unknown_choice_abstains() = runTest {
        val client = JevClient(
            JevTransport {
                JevResponse(
                    answers = mapOf(
                        "moderation_action" to buildJsonObject {
                            put("choice", "unexpected")
                            put("confidence", 0.99)
                        }
                    )
                )
            }
        )

        val result = ModerationDecider(client).decide(ModerationContext("unexpected"))

        assertIs<Decision.Abstained>(result)
        assertEquals("unexpected_jev_choice", result.reason)
    }

    @Test
    fun verification_uses_abstention_for_missing_or_uncertain_result() = runTest {
        val client = JevClient(JevTransport { JevResponse() })

        val result = VerificationDecider(client).verify(
            expected = "payment confirmed",
            observed = "payment pending"
        )

        assertIs<Decision.Abstained>(result)
        assertEquals("missing_jev_answer", result.reason)
    }
}
