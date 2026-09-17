package dev.rocky.data.ai

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import java.net.InetSocketAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnthropicSuggestionClientTest {
    @Test
    fun authenticatesAndGeneratesAGroundedSuggestion() {
        var apiKey = ""
        var version = ""
        var requestBody = ""
        var modelsQuery = ""
        val server = HttpServer.create(InetSocketAddress(0), 0).apply {
            createContext("/v1/models") { exchange ->
                modelsQuery = exchange.requestURI.rawQuery.orEmpty()
                apiKey = exchange.requestHeaders.getFirst("x-api-key")
                version = exchange.requestHeaders.getFirst("anthropic-version")
                exchange.respond("""{"data":[{"id":"claude-haiku-4-5-test"}]}""")
            }
            createContext("/v1/messages") { exchange ->
                apiKey = exchange.requestHeaders.getFirst("x-api-key")
                version = exchange.requestHeaders.getFirst("anthropic-version")
                requestBody = exchange.requestBody.bufferedReader().readText()
                exchange.respond(
                    """{"content":[{"type":"text","text":"{\"suggestion\":\"Responda sobre o preço.\",\"source_message_ids\":[\"m1\"]}"}],"usage":{"input_tokens":11,"output_tokens":7,"cache_creation_input_tokens":13,"cache_read_input_tokens":5}}""",
                )
            }
            start()
        }
        try {
            val endpoint = "http://localhost:${server.address.port}"
            val config = AiProviderConfiguration(AiProviderKind.Anthropic, endpoint, "claude-haiku-4-5-test", "secret-key")
            val client = DesktopAiSuggestionClient(allowTestLoopback = true)

            val connection = client.testConnection(config)
            val models = client.availableModels(config)
            val suggestion = client.generateSuggestion(config, listOf(ChatMessage("m1", "viewer", "Preço?", StreamPlatform.Twitch)))

            assertTrue(connection.successful)
            assertEquals(listOf("claude-haiku-4-5-test"), models)
            assertEquals(36L, suggestion?.reportedTokens)
            assertEquals("limit=1000", modelsQuery)
            assertEquals("secret-key", apiKey)
            assertEquals("2023-06-01", version)
            assertTrue("\"system\"" in requestBody)
            assertTrue("\"output_config\":{\"format\":{\"type\":\"json_schema\"" in requestBody)
            assertTrue("\"max_tokens\":300" in requestBody)
            assertTrue("\"model\":\"claude-haiku-4-5-test\"" in requestBody)
            assertTrue("secret-key" !in requestBody)
        } finally {
            server.stop(0)
        }
    }
}
