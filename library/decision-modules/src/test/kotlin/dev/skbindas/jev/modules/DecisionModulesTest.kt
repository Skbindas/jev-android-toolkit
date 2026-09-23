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
}
