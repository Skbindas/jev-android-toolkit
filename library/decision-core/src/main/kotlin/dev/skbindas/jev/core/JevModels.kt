package dev.skbindas.jev.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull

@Serializable
data class JevRequest(
    val model: String = "jev-latest",
    val state: String,
    val questions: Map<String, JsonObject>
)

@Serializable
data class JevUsage(
    @SerialName("input_tokens") val inputTokens: Int = 0,
    @SerialName("output_tokens") val outputTokens: Int = 0
)

@Serializable
data class JevResponse(
    val model: String? = null,
    val answers: Map<String, JsonObject> = emptyMap(),
    val usage: JevUsage? = null
)

data class ChoiceDecision(
    val key: String,
    val confidence: Double,
    val probabilities: Map<String, Double>
)

data class ScoreDecision(
    val score: Int,
    val confidence: Double
)

data class NoulDecision(
    val probability: Double
)

sealed interface Decision<out T> {
    data class Accepted<T>(val value: T, val confidence: Double) : Decision<T>
    data class Abstained(val reason: String, val confidence: Double? = null) : Decision<Nothing>
}

fun JevResponse.choice(questionId: String): ChoiceDecision? {
    val answer = answers[questionId] ?: return null
    val key = answer["choice"]?.toString()?.trim('"') ?: return null
    val confidence = answer["confidence"]?.jsonPrimitive?.doubleOrNull ?: 0.0
    val probabilities = answer["probabilities"]
        ?.jsonObject
        ?.mapNotNull { (choice, value) ->
            value.jsonPrimitive.doubleOrNull?.let { choice to it }
        }
        ?.toMap()
        ?: emptyMap()

    return ChoiceDecision(key, confidence, probabilities)
}

fun JevResponse.score(questionId: String): ScoreDecision? {
    val answer = answers[questionId] ?: return null
    val score = answer["score"]?.jsonPrimitive?.intOrNull ?: return null
    val confidence = answer["confidence"]?.jsonPrimitive?.doubleOrNull ?: 0.0
    return ScoreDecision(score, confidence)
}

fun JevResponse.noul(questionId: String): NoulDecision? {
    val answer = answers[questionId] ?: return null
    val probability = answer["noul"]?.jsonPrimitive?.doubleOrNull ?: return null
    return NoulDecision(probability)
}
