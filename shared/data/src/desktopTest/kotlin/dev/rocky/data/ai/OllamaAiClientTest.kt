package dev.rocky.data.ai

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import java.net.InetSocketAddress
import java.net.http.HttpClient
import java.nio.charset.StandardCharsets
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class OllamaAiClientTest {
    @Test
    fun testsModelAndGeneratesGroundedSuggestion() {
        var requestBody = ""
        val server = HttpServer.create(InetSocketAddress(0), 0).apply {
            createContext("/api/tags") { exchange ->
                exchange.respond("""{"models":[{"name":"llama3.2:latest"}]}""")
            }
            createContext("/api/chat") { exchange ->
                requestBody = exchange.requestBody.bufferedReader().readText()
                exchange.respond(
                    """{"message":{"content":"{\"suggestion\":\"Responda sobre o preço.\",\"source_message_ids\":[\"m1\"]}"}}""",
                )
            }
            start()
        }
        try {
            val endpoint = "http://localhost:${server.address.port}"
            val client = OllamaAiClient(HttpClient.newHttpClient())
            val connection = client.testConnection(endpoint, "llama3.2")
            val suggestion = client.generate(endpoint, "llama3.2", listOf(message))

            assertTrue(connection.successful)
            assertFalse(client.testConnection(endpoint, "llama3.2:70b").successful)
            assertEquals("Responda sobre o preço.", suggestion?.text)
            assertEquals(setOf("m1"), suggestion?.sourceMessageIds)
            assertTrue("não confiável" in requestBody)
            requestBody = ""
            val probe = DesktopAiSuggestionClient().testConnection(
                AiProviderConfiguration(AiProviderKind.Ollama, endpoint, "llama3.2"),
            )
            assertTrue(probe.successful)
            assertTrue("Rocky test" in requestBody)
        } finally {
            server.stop(0)
        }
    }

    @Test fun listsModelsAndAnswersWithoutNewChatThroughDesktopClient() {
        val server = HttpServer.create(InetSocketAddress(0), 0).apply {
            createContext("/api/tags") { it.respond("""{"models":[{"name":"local-model"}]}""") }
            createContext("/api/chat") {
                it.respond("""{"message":{"content":"{\"suggestion\":\"Sem mensagens recentes.\",\"source_message_ids\":[]}"}}""")
            }
            start()
        }
        try {
            val config = AiProviderConfiguration(AiProviderKind.Ollama, "http://localhost:${server.address.port}", "local-model")
            val client = DesktopAiSuggestionClient()
            assertEquals(listOf("local-model"), client.availableModels(config.copy(model = "")))
            val answer = client.generateSuggestion(config, emptyList(), "O que o chat achou?", dev.rocky.core.agent.AgentConfiguration())
            assertEquals("Sem mensagens recentes.", answer?.text)
        } finally { server.stop(0) }
    }

    private val message = ChatMessage("m1", "viewer", "Qual é o preço?", StreamPlatform.Twitch)
}

internal fun com.sun.net.httpserver.HttpExchange.respond(body: String) {
    val bytes = body.toByteArray(StandardCharsets.UTF_8)
    sendResponseHeaders(200, bytes.size.toLong())
    responseBody.use { it.write(bytes) }
}
