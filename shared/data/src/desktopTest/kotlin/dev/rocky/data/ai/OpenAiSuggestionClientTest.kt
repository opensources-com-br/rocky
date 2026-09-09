package dev.rocky.data.ai

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import java.net.InetSocketAddress
import java.net.http.HttpClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OpenAiSuggestionClientTest {
    @Test
    fun authenticatesAndRequestsNonStoredStructuredResponse() {
        var authorization = ""
        var requestBody = ""
        val server = HttpServer.create(InetSocketAddress(0), 0).apply {
            createContext("/v1/models/gpt-test") { exchange ->
                authorization = exchange.requestHeaders.getFirst("Authorization")
                exchange.respond("""{"id":"gpt-test"}""")
            }
            createContext("/v1/responses") { exchange ->
                authorization = exchange.requestHeaders.getFirst("Authorization")
                requestBody = exchange.requestBody.bufferedReader().readText()
                exchange.respond(
                    """{"output":[{"type":"message","content":[{"type":"output_text","text":"{\"suggestion\":\"Responda a dúvida.\",\"source_message_ids\":[\"m1\"]}"}]}]}""",
                )
            }
            start()
        }
        try {
            val endpoint = "http://localhost:${server.address.port}"
            val client = OpenAiSuggestionClient(HttpClient.newHttpClient())
            val connection = client.testConnection(endpoint, "secret-key", "gpt-test")
            val suggestion = client.generate(endpoint, "secret-key", "gpt-test", listOf(message))

            assertTrue(connection.successful)
            assertEquals("Bearer secret-key", authorization)
            assertEquals("Responda a dúvida.", suggestion?.text)
            assertTrue("\"store\":false" in requestBody)
            assertTrue("\"type\":\"json_schema\"" in requestBody)
            assertTrue("secret-key" !in requestBody)
        } finally {
            server.stop(0)
        }
    }

    private val message = ChatMessage("m1", "viewer", "Pode explicar?", StreamPlatform.Twitch)
}
