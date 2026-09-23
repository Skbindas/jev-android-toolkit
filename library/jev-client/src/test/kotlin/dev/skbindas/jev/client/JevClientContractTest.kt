package dev.skbindas.jev.client

import dev.skbindas.jev.core.JevResponse
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

class JevClientContractTest {
    @Test
    fun evaluate_preserves_multiple_typed_questions_in_one_request() = runTest {
        var calls = 0
        var count = 0
        val client = JevClient(
            JevTransport { request ->
                calls += 1
                count = request.questions.size
                JevResponse()
            }
        )

        client.evaluate(
            state = "A user opened the app.",
            questions = mapOf(
                "a" to dev.skbindas.jev.core.JevQuestions.noul("Is this urgent?"),
                "b" to dev.skbindas.jev.core.JevQuestions.score("Rate urgency.")
            )
        )

        assertEquals(1, calls)
        assertEquals(2, count)
    }

    @Test
    fun choice_response_is_parsed_from_typed_json() = runTest {
        val response = JevResponse(
            answers = mapOf(
                "route" to buildJsonObject {
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

        val result = client.choice(
            "borderline",
            "route",
            "Choose the route.",
            mapOf("review" to "review", "allow" to "allow", "reject" to "reject")
        )

        assertEquals("review", result?.key)
        assertEquals(0.93, result?.probabilities?.get("review"))
    }
}
