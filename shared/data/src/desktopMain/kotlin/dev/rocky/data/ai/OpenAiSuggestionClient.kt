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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

internal class OpenAiSuggestionClient(private val httpClient: HttpClient) {
    fun testConnection(
        endpoint: String,
        apiKey: String,
        model: String,
        modelPath: String = "/v1/models/${model.urlEncode()}",
        providerName: String = "OpenAI",
    ): AiConnectionResult = runCatching {
        httpClient.send(
            request(endpoint, modelPath, apiKey).GET().build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireOpenAiSuccess()
        AiConnectionResult(true, "$providerName conectado · $model disponível")
    }.getOrElse { AiConnectionResult(false, it.userMessage("Não foi possível conectar ao $providerName")) }

    fun generate(
        endpoint: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        streamerRequest: String? = null,
        agent: AgentConfiguration = AgentConfiguration(),
    ): AiGeneratedSuggestion? {
        val prompt = buildAiSuggestionPrompt(messages, streamerRequest, agent)
        val schema = suggestionSchema()
        val body = buildJsonObject {
            put("model", model)
            put("instructions", prompt.instructions)
            put("input", prompt.input)
            put("store", false)
            put("max_output_tokens", 300)
            put("text", buildJsonObject {
                put("format", buildJsonObject {
                    put("type", "json_schema")
                    put("name", "rocky_suggestion")
                    put("strict", true)
                    put("schema", schema)
                })
            })
        }.toString()
        val response = httpClient.send(
            request(endpoint, "/v1/responses", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireOpenAiSuccess()
        return AiSuggestionPayloads.suggestion(
            AiSuggestionPayloads.openAiText(response.body()),
            prompt.messageIds,
        )
    }

    fun generateOpenRouter(
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
            put("max_tokens", 300)
            put("response_format", buildJsonObject {
                put("type", "json_schema")
                put("json_schema", buildJsonObject {
                    put("name", "rocky_suggestion")
                    put("strict", true)
                    put("schema", suggestionSchema())
                })
            })
            put("provider", buildJsonObject { put("require_parameters", true) })
        }.toString()
        val response = httpClient.send(
            request(endpoint, "/v1/chat/completions", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireOpenAiSuccess()
        return AiSuggestionPayloads.suggestion(
            AiSuggestionPayloads.openRouterText(response.body()),
            prompt.messageIds,
        )
    }

    private fun request(endpoint: String, path: String, apiKey: String): HttpRequest.Builder =
        HttpRequest.newBuilder(URI.create("${endpoint.trim().trimEnd('/')}$path"))
            .timeout(Duration.ofSeconds(45))
            .header("Authorization", "Bearer ${apiKey.trim()}")

    private fun String.urlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
}

private fun suggestionSchema() = buildJsonObject {
    put("type", "object")
    put("additionalProperties", false)
    put("properties", buildJsonObject {
        put("suggestion", buildJsonObject { put("type", "string") })
        put("source_message_ids", buildJsonObject {
            put("type", "array")
            put("items", buildJsonObject { put("type", "string") })
        })
    })
    put("required", buildJsonArray {
        add(JsonPrimitive("suggestion"))
        add(JsonPrimitive("source_message_ids"))
    })
}

private fun HttpResponse<String>.requireOpenAiSuccess(): HttpResponse<String> {
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
