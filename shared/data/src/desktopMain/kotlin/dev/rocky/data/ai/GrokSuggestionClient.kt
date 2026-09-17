package dev.rocky.data.ai

import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.live.ChatMessage
import java.net.http.HttpClient

/** xAI's Responses API follows the OpenAI-compatible Responses contract. */
internal class GrokSuggestionClient(httpClient: HttpClient) {
    private val responses = OpenAiSuggestionClient(httpClient)

    fun testConnection(endpoint: String, apiKey: String, model: String): AiConnectionResult =
        responses.testConnection(
            endpoint, apiKey, model,
            modelPath = "/v1/language-models/${model.urlEncode()}", providerName = "Grok",
        )

    fun generate(
        endpoint: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        streamerRequest: String? = null,
        agent: AgentConfiguration = AgentConfiguration(),
    ): AiGeneratedSuggestion? = responses.generate(
        endpoint, apiKey, model, messages, streamerRequest, agent,
        promptCacheKey = "rocky-grok-suggestion-v1",
        reasoningEffort = "low".takeIf { model.startsWith("grok-4.5") || model.startsWith("grok-4.6") },
        providerName = "Grok",
        maxOutputTokens = 600,
    )
}

private fun String.urlEncode(): String = java.net.URLEncoder.encode(this, java.nio.charset.StandardCharsets.UTF_8)
