package dev.rocky.data.ai

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import java.net.InetSocketAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GrokSuggestionClientTest {
    @Test
    fun authenticatesListsModelsAndGeneratesAGroundedSuggestion() {
        var authorization = ""
        var requestBody = ""
        val server = HttpServer.create(InetSocketAddress(0), 0).apply {
            createContext("/v1/language-models") { exchange ->
                authorization = exchange.requestHeaders.getFirst("Authorization")
                exchange.respond("""{"models":[{"id":"grok-4.6-test"}]}""")
            }
            createContext("/v1/language-models/grok-4.6-test") { exchange ->
                authorization = exchange.requestHeaders.getFirst("Authorization")
                exchange.respond("""{"id":"grok-4.6-test"}""")
            }
            createContext("/v1/responses") { exchange ->
                authorization = exchange.requestHeaders.getFirst("Authorization")
                requestBody = exchange.requestBody.bufferedReader().readText()
                exchange.respond(
                    """{"output":[{"type":"message","content":[{"type":"output_text","text":"{\"suggestion\":\"Responda sobre o preço.\",\"source_message_ids\":[\"m1\"]}"}]}],"usage":{"input_tokens":11,"output_tokens":7}}""",
                )
            }
            start()
        }
        try {
            val endpoint = "http://localhost:${server.address.port}"
            val config = AiProviderConfiguration(AiProviderKind.Grok, endpoint, "grok-4.6-test", "xai-key")
            val client = DesktopAiSuggestionClient(allowTestLoopback = true)

            val connection = client.testConnection(config)
            val models = client.availableModels(config)

            assertTrue(connection.successful)
            assertEquals(listOf("grok-4.6-test"), models)
            assertEquals("Bearer xai-key", authorization)
            assertTrue("\"model\":\"grok-4.6-test\"" in requestBody)
            assertTrue("\"text\":{\"format\":{\"type\":\"json_schema\"" in requestBody)
            assertTrue("\"prompt_cache_key\":\"rocky-grok-suggestion-v1\"" in requestBody)
            assertTrue("\"reasoning\":{\"effort\":\"low\"}" in requestBody)
            assertTrue("\"max_output_tokens\":300" in requestBody)
            assertTrue("xai-key" !in requestBody)
        } finally {
            server.stop(0)
        }
    }
}
