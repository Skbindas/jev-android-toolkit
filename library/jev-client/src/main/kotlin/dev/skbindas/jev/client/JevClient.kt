package dev.skbindas.jev.client

import dev.skbindas.jev.core.JevQuestions
import dev.skbindas.jev.core.JevRequest
import dev.skbindas.jev.core.JevResponse
import dev.skbindas.jev.core.choice
import dev.skbindas.jev.core.noul
import dev.skbindas.jev.core.score
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI

fun interface JevTransport {
    suspend fun execute(request: JevRequest): JevResponse
}

class JevClient(private val transport: JevTransport) {
    suspend fun evaluate(
        state: String,
        questions: Map<String, JsonObject>
    ): JevResponse = transport.execute(
        JevRequest(state = state, questions = questions)
    )

    suspend fun choice(
        state: String,
        questionId: String,
        instructions: String,
        criteria: Map<String, String>
    ) = evaluate(
        state,
        mapOf(questionId to JevQuestions.choice(instructions, criteria))
    ).choice(questionId)

    suspend fun score(
        state: String,
        questionId: String,
        instructions: String,
        criteria: Map<String, String> = emptyMap()
    ) = evaluate(
        state,
        mapOf(questionId to JevQuestions.score(instructions, criteria))
    ).score(questionId)

    suspend fun noul(
        state: String,
        questionId: String,
        instructions: String
    ) = evaluate(
        state,
        mapOf(questionId to JevQuestions.noul(instructions))
    ).noul(questionId)
}

class HttpJevTransport(
    private val apiKey: String,
    private val endpoint: String = "https://api.typesafe.ai/v1/systemone"
) : JevTransport {
    init {
        require(apiKey.isNotBlank()) { "A TypeSafe API key is required for live Jev calls." }
    }

    override suspend fun execute(request: JevRequest): JevResponse = withContext(Dispatchers.IO) {
        val connection = URI(endpoint).toURL().openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 15_000
            connection.readTimeout = 30_000
            connection.doOutput = true
            connection.setRequestProperty("Authorization", "Bearer " + apiKey)
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")

            val payload = Json.encodeToString(JevRequest.serializer(), request)
            connection.outputStream.use { stream ->
                stream.writer(Charsets.UTF_8).use { writer -> writer.write(payload) }
            }

            val status = connection.responseCode
            val stream = if (status in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream ?: connection.inputStream
            }
            val body = stream.use {
                BufferedReader(InputStreamReader(it, Charsets.UTF_8)).readText()
            }

            if (status !in 200..299) {
                error("TypeSafe API returned HTTP " + status + ": " + body)
            }

            Json.decodeFromString(JevResponse.serializer(), body)
        } finally {
            connection.disconnect()
        }
    }
}
