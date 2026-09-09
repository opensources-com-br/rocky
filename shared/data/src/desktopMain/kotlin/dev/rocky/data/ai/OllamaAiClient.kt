package dev.rocky.data.ai

import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.buildAiSuggestionPrompt
import dev.rocky.core.live.ChatMessage
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class OllamaAiClient(private val httpClient: HttpClient) {
    fun testConnection(endpoint: String, model: String): AiConnectionResult = runCatching {
        val response = httpClient.send(
            request(endpoint, "/api/tags").GET().build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireSuccess()
        val models = AiSuggestionPayloads.ollamaModels(response.body())
        val available = models.any { it == model || it.substringBefore(':') == model.substringBefore(':') }
        if (available) {
            AiConnectionResult(true, "Ollama conectado · $model disponível")
        } else {
            AiConnectionResult(false, "Ollama conectado, mas o modelo $model não foi encontrado")
        }
    }.getOrElse { AiConnectionResult(false, it.userMessage("Não foi possível conectar ao Ollama")) }

    fun generate(endpoint: String, model: String, messages: List<ChatMessage>): AiGeneratedSuggestion? {
        val prompt = buildAiSuggestionPrompt(messages)
        val body = buildJsonObject {
            put("model", model)
            put("stream", false)
            put("format", "json")
            put("keep_alive", "10m")
            put("messages", buildJsonArray {
                add(buildJsonObject {
                    put("role", "system")
                    put("content", prompt.instructions)
                })
                add(buildJsonObject {
                    put("role", "user")
                    put("content", prompt.input)
                })
            })
        }.toString()
        val response = httpClient.send(
            request(endpoint, "/api/chat")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireSuccess()
        return AiSuggestionPayloads.suggestion(
            AiSuggestionPayloads.ollamaText(response.body()),
            prompt.messageIds,
        )
    }

    private fun request(endpoint: String, path: String): HttpRequest.Builder = HttpRequest.newBuilder(
        URI.create("${endpoint.trim().trimEnd('/')}$path"),
    ).timeout(Duration.ofSeconds(45))
}

internal class AiProviderException(val statusCode: Int) : Exception("AI provider returned HTTP $statusCode")

private fun HttpResponse<String>.requireSuccess(): HttpResponse<String> {
    if (statusCode() !in 200..299) throw AiProviderException(statusCode())
    return this
}

internal fun Throwable.userMessage(fallback: String): String = when (this) {
    is AiProviderException -> "$fallback (HTTP $statusCode)"
    else -> fallback
}
