package dev.skbindas.jev.client

import com.sun.net.httpserver.HttpServer
import dev.skbindas.jev.core.JevQuestions
import dev.skbindas.jev.core.JevRequest
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HttpJevTransportContractTest {
    @Test
    fun posts_typed_request_and_parses_response() = runTest {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/v1/systemone") { exchange ->
            assertEquals("Bearer test-key", exchange.requestHeaders.getFirst("Authorization"))
            assertEquals("application/json", exchange.requestHeaders.getFirst("Content-Type"))
            val requestBody = exchange.requestBody.use { it.readAllBytes().toString(StandardCharsets.UTF_8) }
            assertTrue(requestBody.contains("\"route\""))
            val body = """{"model":"jev-test","answers":{},"usage":{"input_tokens":4,"output_tokens":2}}"""
            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, body.toByteArray(StandardCharsets.UTF_8).size.toLong())
            exchange.responseBody.use { it.write(body.toByteArray(StandardCharsets.UTF_8)) }
        }
        server.start()
        try {
            val endpoint = "http://127.0.0.1:${server.address.port}/v1/systemone"
            val response = HttpJevTransport("test-key", endpoint).execute(
                JevRequest(
                    state = "a user opened the app",
                    questions = mapOf("route" to JevQuestions.noul("Is this urgent?"))
                )
            )
            assertEquals("jev-test", response.model)
            assertEquals(4, response.usage?.inputTokens)
            assertEquals(2, response.usage?.outputTokens)
        } finally {
            server.stop(0)
        }
    }

    @Test
    fun non_success_response_is_exposed_as_an_exception() = runTest {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/error") { exchange ->
            val body = "rate limited".toByteArray(StandardCharsets.UTF_8)
            exchange.sendResponseHeaders(429, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }
        server.start()
        try {
            val endpoint = "http://127.0.0.1:${server.address.port}/error"
            val error = assertFailsWith<IllegalStateException> {
                HttpJevTransport("test-key", endpoint).execute(
                    JevRequest(state = "state", questions = emptyMap())
                )
            }
            assertTrue(error.message.orEmpty().contains("HTTP 429"))
        } finally {
            server.stop(0)
        }
    }
}
