package dev.rocky.data.ai

import dev.rocky.core.agent.AgentConfiguration
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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

internal class AnthropicSuggestionClient(private val httpClient: HttpClient) {
    fun testConnection(endpoint: String, apiKey: String): AiConnectionResult = runCatching {
        httpClient.send(
            request(endpoint, "/v1/models", apiKey).GET().build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireAnthropicSuccess()
        AiConnectionResult(true, "Anthropic conectado")
    }.getOrElse { AiConnectionResult(false, it.userMessage("Não foi possível conectar à Anthropic")) }

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
            put("model", model)
            put("max_tokens", 300)
            put("system", prompt.instructions)
            if (supportsAnthropicStructuredOutput(model)) put("output_config", buildJsonObject {
                put("format", buildJsonObject {
                    put("type", "json_schema")
                    put("schema", suggestionSchema())
                })
            })
            put("messages", buildJsonArray {
                add(buildJsonObject {
                    put("role", "user")
                    put("content", prompt.input)
                })
            })
        }.toString()
        val response = httpClient.send(
            request(endpoint, "/v1/messages", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireAnthropicSuccess()
        return AiSuggestionPayloads.suggestion(
            AiSuggestionPayloads.anthropicText(response.body()),
            prompt.messageIds,
        )?.copy(reportedTokens = reportedAnthropicTokenCount(response.body()))
    }

    private fun request(endpoint: String, path: String, apiKey: String): HttpRequest.Builder =
        HttpRequest.newBuilder(URI.create("${endpoint.trim().trimEnd('/')}$path"))
            .timeout(Duration.ofSeconds(45))
            .header("x-api-key", apiKey.trim())
            .header("anthropic-version", ANTHROPIC_VERSION)
}

private const val ANTHROPIC_VERSION = "2023-06-01"

internal fun supportsAnthropicStructuredOutput(model: String): Boolean {
    val version = Regex("""^claude-(?:haiku|sonnet|opus)-(\d+)(?:-(\d+))?""").find(model) ?: return false
    val major = version.groupValues[1].toInt()
    val minor = version.groupValues[2].toIntOrNull() ?: 0
    return major > 4 || major == 4 && minor >= 5
}

internal fun reportedAnthropicTokenCount(body: String): Long? = runCatching {
    val usage = kotlinx.serialization.json.Json.parseToJsonElement(body).jsonObject["usage"]?.jsonObject ?: return null
    val keys = listOf("input_tokens", "output_tokens", "cache_creation_input_tokens", "cache_read_input_tokens")
    val tokens = keys.map { key ->
        usage[key]?.jsonPrimitive?.content?.toLongOrNull() ?: if (key.startsWith("cache_")) 0 else return null
    }
    tokens.sum().takeIf { tokens.all { it >= 0 } }
}.getOrNull()

private fun HttpResponse<String>.requireAnthropicSuccess(): HttpResponse<String> {
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
