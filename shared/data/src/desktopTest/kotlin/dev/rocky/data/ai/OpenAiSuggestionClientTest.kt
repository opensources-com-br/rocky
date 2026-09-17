package dev.rocky.data.ai

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import java.net.InetSocketAddress
import java.net.http.HttpClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OpenAiSuggestionClientTest {
    @Test
    fun validatesOpenRouterAndGeneratesWithTheFreeRouter() {
        var requestBody = ""
        var generationAttempts = 0
        var validatedKey = false
        var appTitle = ""
        var modelsQuery = ""
        val server = HttpServer.create(InetSocketAddress(0), 0).apply {
            createContext("/v1/key") { exchange ->
                validatedKey = true
                exchange.respond("""{"data":{"label":"rocky-test"}}""")
            }
            createContext("/v1/models") { exchange ->
                modelsQuery = exchange.requestURI.rawQuery.orEmpty()
                exchange.respond("""{"data":[{"id":"openrouter/free"}]}""")
            }
            createContext("/v1/chat/completions") { exchange ->
                appTitle = exchange.requestHeaders.getFirst("X-OpenRouter-Title")
                requestBody = exchange.requestBody.bufferedReader().readText()
                generationAttempts += 1
                val response =
                    """{"choices":[{"message":{"content":"{\"suggestion\":\"Teste aprovado.\",\"source_message_ids\":[\"m1\"]}"}}]}"""
                exchange.respond(response)
            }
            start()
        }
        try {
            val result = DesktopAiSuggestionClient(allowTestLoopback = true).testConnection(
                AiProviderConfiguration(
                    AiProviderKind.OpenRouter,
                    "http://localhost:${server.address.port}",
                    "openrouter/free",
                    "router-key",
                ),
            )

            assertTrue(result.successful)
            assertTrue(validatedKey)
            assertEquals("Rocky", appTitle)
            DesktopAiSuggestionClient(allowTestLoopback = true).availableModels(
                AiProviderConfiguration(AiProviderKind.OpenRouter,
                    "http://localhost:${server.address.port}", "list", "router-key"),
            )
            assertEquals("supported_parameters=response_format", modelsQuery)
            assertEquals("Conexão e geração verificadas", result.message)
            assertTrue("\"response_format\"" in requestBody)
            assertTrue("\"require_parameters\":true" in requestBody)
            assertEquals(1, generationAttempts)
        } finally {
            server.stop(0)
        }
    }

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
            val suggestion = client.generate(endpoint, "secret-key", "gpt-test", listOf(message),
                promptCacheKey = "rocky-suggestion-v1")

            assertTrue(connection.successful)
            assertEquals("Bearer secret-key", authorization)
            assertEquals("Responda a dúvida.", suggestion?.text)
            assertTrue("\"store\":false" in requestBody)
            assertTrue("\"max_output_tokens\":600" in requestBody)
            assertTrue("\"prompt_cache_key\":\"rocky-suggestion-v1\"" in requestBody)
            assertTrue("\"type\":\"json_schema\"" in requestBody)
            assertTrue("secret-key" !in requestBody)
        } finally {
            server.stop(0)
        }
    }

    private val message = ChatMessage("m1", "viewer", "Pode explicar?", StreamPlatform.Twitch)
}
