package dev.skbindas.jev.core

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

object JevQuestions {
    fun choice(instructions: String, criteria: Map<String, String>): JsonObject =
        buildJsonObject {
            put("type", "choice")
            put("instructions", instructions)
            putJsonObject("criteria") {
                criteria.forEach { (key, value) -> put(key, value) }
            }
        }

    fun score(instructions: String, criteria: Map<String, String> = emptyMap()): JsonObject =
        buildJsonObject {
            put("type", "score")
            put("instructions", instructions)
            if (criteria.isNotEmpty()) {
                putJsonObject("criteria") {
                    criteria.forEach { (key, value) -> put(key, value) }
                }
            }
        }

    fun noul(instructions: String): JsonObject =
        buildJsonObject {
            put("type", "noul")
            put("instructions", instructions)
        }
}
