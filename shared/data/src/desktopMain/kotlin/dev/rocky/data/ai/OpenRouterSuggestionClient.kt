package dev.rocky.data.ai

import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.live.ChatMessage
import java.net.http.HttpClient

internal class OpenRouterSuggestionClient(httpClient: HttpClient) {
    private val delegate = OpenAiSuggestionClient(httpClient)

    fun testConnection(endpoint: String, apiKey: String, model: String): AiConnectionResult =
        delegate.testConnection(endpoint, apiKey, model, "/v1/key", "OpenRouter").let { result ->
            if (result.successful) result.copy(message = "OpenRouter conectado · chave válida") else result
        }

    fun generate(endpoint: String, apiKey: String, model: String, messages: List<ChatMessage>,
        streamerRequest: String?, agent: AgentConfiguration): AiGeneratedSuggestion? =
        delegate.generateOpenRouter(endpoint, apiKey, model, messages, streamerRequest, agent)
}
