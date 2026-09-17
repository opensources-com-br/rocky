package dev.rocky.data.ai

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import java.net.InetSocketAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeminiSuggestionClientTest {
    @Test
    fun authenticatesListsModelsAndGeneratesAGroundedSuggestion() {
        var apiKey = ""
        var requestBody = ""
        var modelsQuery = ""
        val server = HttpServer.create(InetSocketAddress(0), 0).apply {
            createContext("/v1beta/models") { exchange ->
                modelsQuery = exchange.requestURI.rawQuery.orEmpty()
                apiKey = exchange.requestHeaders.getFirst("x-goog-api-key")
                exchange.respond("""{"models":[{"name":"models/gemini-test","supportedGenerationMethods":["generateContent"]}]}""")
            }
            createContext("/v1beta/models/gemini-test") { exchange ->
                apiKey = exchange.requestHeaders.getFirst("x-goog-api-key")
                exchange.respond("""{"name":"models/gemini-test"}""")
            }
            createContext("/v1beta/models/gemini-test:generateContent") { exchange ->
                apiKey = exchange.requestHeaders.getFirst("x-goog-api-key")
                requestBody = exchange.requestBody.bufferedReader().readText()
                exchange.respond(
                    """{"candidates":[{"content":{"parts":[{"text":"{\"suggestion\":\"Responda sobre o preço.\",\"source_message_ids\":[\"m1\"]}"}]}}],"usageMetadata":{"promptTokenCount":11,"candidatesTokenCount":7,"thoughtsTokenCount":3,"totalTokenCount":21}}""",
                )
            }
            start()
        }
        try {
            val endpoint = "http://localhost:${server.address.port}"
            val config = AiProviderConfiguration(AiProviderKind.Gemini, endpoint, "gemini-test", "google-key")
            val client = DesktopAiSuggestionClient(allowTestLoopback = true)

            val connection = client.testConnection(config)
            val models = client.availableModels(config)

            assertTrue(connection.successful)
            assertEquals(listOf("gemini-test"), models)
            assertEquals("pageSize=1000", modelsQuery)
            assertEquals("google-key", apiKey)
            assertTrue("\"systemInstruction\"" in requestBody)
            assertTrue("\"responseMimeType\":\"application/json\"" in requestBody)
            assertTrue("\"responseJsonSchema\"" in requestBody)
            assertTrue("\"description\"" in requestBody)
            assertTrue("\"maxOutputTokens\":300" in requestBody)
            assertTrue("google-key" !in requestBody)
        } finally {
            server.stop(0)
        }
    }
}
