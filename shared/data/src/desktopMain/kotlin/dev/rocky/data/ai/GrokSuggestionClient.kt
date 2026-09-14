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
        responses.testConnection(endpoint, apiKey, model, providerName = "Grok")

    fun generate(
        endpoint: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        streamerRequest: String? = null,
        agent: AgentConfiguration = AgentConfiguration(),
    ): AiGeneratedSuggestion? = responses.generate(endpoint, apiKey, model, messages, streamerRequest, agent)
}
