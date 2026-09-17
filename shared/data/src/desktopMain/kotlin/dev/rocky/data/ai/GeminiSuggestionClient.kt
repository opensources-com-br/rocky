package dev.rocky.data.ai

import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.buildAiSuggestionPrompt
import dev.rocky.core.live.ChatMessage
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

internal class GeminiSuggestionClient(private val httpClient: HttpClient) {
    fun testConnection(endpoint: String, apiKey: String, model: String): AiConnectionResult = runCatching {
        httpClient.send(
            request(endpoint, "/v1beta/models/${model.modelId().urlEncode()}", apiKey).GET().build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireGeminiSuccess()
        AiConnectionResult(true, "Gemini conectado · $model disponível")
    }.getOrElse { AiConnectionResult(false, it.userMessage("Não foi possível conectar ao Gemini")) }

    fun generate(
        endpoint: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        streamerRequest: String? = null,
        agent: AgentConfiguration = AgentConfiguration(),
    ): AiGeneratedSuggestion? {
        val prompt = buildAiSuggestionPrompt(messages, streamerRequest, agent)
        val body = buildJsonObject {
            put("systemInstruction", buildJsonObject {
                put("parts", buildJsonArray { add(buildJsonObject { put("text", prompt.instructions) }) })
            })
            put("contents", buildJsonArray {
                add(buildJsonObject {
                    put("role", "user")
                    put("parts", buildJsonArray { add(buildJsonObject { put("text", prompt.input) }) })
                })
            })
            put("generationConfig", buildJsonObject {
                put("maxOutputTokens", 300)
                put("responseMimeType", "application/json")
                put("responseJsonSchema", suggestionSchema())
            })
        }.toString()
        val response = httpClient.send(
            request(endpoint, "/v1beta/models/${model.modelId().urlEncode()}:generateContent", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireGeminiSuccess()
        return AiSuggestionPayloads.suggestion(
            AiSuggestionPayloads.geminiText(response.body()),
            prompt.messageIds,
        )?.copy(reportedTokens = reportedTotalTokenCount(response.body(), "totalTokenCount", "usageMetadata"))
    }

    private fun request(endpoint: String, path: String, apiKey: String): HttpRequest.Builder =
        HttpRequest.newBuilder(URI.create("${endpoint.trim().trimEnd('/')}$path"))
            .timeout(Duration.ofSeconds(45))
            .header("x-goog-api-key", apiKey.trim())

    private fun String.modelId(): String = removePrefix("models/")
    private fun String.urlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
}

private fun HttpResponse<String>.requireGeminiSuccess(): HttpResponse<String> {
    if (statusCode() !in 200..299) {
        val providerMessage = runCatching {
            kotlinx.serialization.json.Json.parseToJsonElement(body())
                .jsonObject["error"]
                ?.jsonObject
                ?.get("message")
                ?.jsonPrimitive
                ?.content
                ?.take(180)
        }.getOrNull()
        throw AiProviderException(statusCode(), providerMessage)
    }
    return this
}
