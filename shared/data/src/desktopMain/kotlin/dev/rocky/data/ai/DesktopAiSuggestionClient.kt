package dev.rocky.data.ai

import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.core.ai.AiSuggestionClient
import dev.rocky.core.live.ChatMessage
import java.net.http.HttpClient
import java.time.Duration

class DesktopAiSuggestionClient : AiSuggestionClient {
    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()
    private val ollama = OllamaAiClient(httpClient)
    private val openAi = OpenAiSuggestionClient(httpClient)

    override fun testConnection(configuration: AiProviderConfiguration): AiConnectionResult {
        val validation = configuration.validationError()
        if (validation != null) return AiConnectionResult(false, validation)
        return when (configuration.provider) {
            AiProviderKind.Ollama -> ollama.testConnection(configuration.endpoint, configuration.model)
            AiProviderKind.OpenAI -> openAi.testConnection(
                configuration.endpoint,
                configuration.apiKey,
                configuration.model,
            )
        }
    }

    override fun generateSuggestion(
        configuration: AiProviderConfiguration,
        messages: List<ChatMessage>,
        streamerRequest: String?,
        agent: AgentConfiguration,
    ): AiGeneratedSuggestion? {
        configuration.validationError()?.let { throw IllegalArgumentException(it) }
        require(messages.isNotEmpty()) { "At least one chat message is required" }
        return when (configuration.provider) {
            AiProviderKind.Ollama -> ollama.generate(
                configuration.endpoint,
                configuration.model,
                messages,
                streamerRequest,
                agent,
            )
            AiProviderKind.OpenAI -> openAi.generate(
                configuration.endpoint,
                configuration.apiKey,
                configuration.model,
                messages,
                streamerRequest,
                agent,
            )
        }
    }

    override fun close() = Unit
}

private fun AiProviderConfiguration.validationError(): String? = when {
    endpoint.isBlank() -> "Informe o endereço do provedor"
    model.isBlank() -> "Informe o modelo"
    provider == AiProviderKind.OpenAI && apiKey.isBlank() -> "Informe a API key da OpenAI"
    else -> null
}
