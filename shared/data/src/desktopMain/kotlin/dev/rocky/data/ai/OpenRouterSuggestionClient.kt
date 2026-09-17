package dev.rocky.data.ai

import dev.rocky.core.ai.AiConnectionResult
import java.net.http.HttpClient

internal class OpenRouterSuggestionClient(httpClient: HttpClient) {
    private val delegate = OpenAiSuggestionClient(httpClient)

    fun testConnection(endpoint: String, apiKey: String, model: String): AiConnectionResult =
        delegate.testConnection(endpoint, apiKey, model, "/v1/key", "OpenRouter").let { result ->
            if (result.successful) result.copy(message = "OpenRouter conectado · chave válida") else result
        }
}
