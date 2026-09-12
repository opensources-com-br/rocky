package dev.rocky.data.ai

import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.core.ai.AiSuggestionClient
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import java.net.http.HttpClient
import java.time.Duration

class DesktopAiSuggestionClient internal constructor(private val allowTestLoopback: Boolean) : AiSuggestionClient {
    constructor() : this(false)
    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()
    private val ollama = OllamaAiClient(httpClient)
    private val openAi = OpenAiSuggestionClient(httpClient)

    override fun testConnection(configuration: AiProviderConfiguration): AiConnectionResult {
        val validation = configuration.validationError(allowTestLoopback)
        if (validation != null) return AiConnectionResult(false, validation)
        val connection = when (configuration.provider) {
            AiProviderKind.Ollama -> ollama.testConnection(configuration.endpoint, configuration.model)
            AiProviderKind.OpenAI -> openAi.testConnection(
                configuration.endpoint,
                configuration.apiKey,
                configuration.model,
            )
            AiProviderKind.OpenRouter -> openAi.testConnection(
                configuration.endpoint,
                configuration.apiKey,
                configuration.model,
                "/v1/model/${configuration.model}",
                "OpenRouter",
            )
        }
        if (!connection.successful) return connection
        return runCatching {
            val probe = ChatMessage("m1", "Rocky test", "Qual é o assunto da live?", StreamPlatform.Twitch)
            generateSuggestion(configuration, listOf(probe), "Responda à pergunta do chat.", AgentConfiguration())
            AiConnectionResult(true, "Conexão e geração verificadas")
        }.getOrElse { AiConnectionResult(false, it.userMessage("Falha ao testar geração")) }
    }

    override fun generateSuggestion(
        configuration: AiProviderConfiguration,
        messages: List<ChatMessage>,
        streamerRequest: String?,
        agent: AgentConfiguration,
    ): AiGeneratedSuggestion? {
        configuration.validationError(allowTestLoopback)?.let { throw IllegalArgumentException(it) }
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
            AiProviderKind.OpenRouter -> openAi.generateOpenRouter(
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

internal fun AiProviderConfiguration.validationError(allowTestLoopback: Boolean = false): String? = when {
    !safeEndpoint(endpoint, provider == AiProviderKind.Ollama || allowTestLoopback) ->
        "Use HTTPS para APIs remotas. HTTP é permitido apenas para Ollama em loopback."
    endpoint.isBlank() -> "Informe o endereço do provedor"
    model.isBlank() -> "Informe o modelo"
    provider != AiProviderKind.Ollama && apiKey.isBlank() -> "Informe a API key do provedor"
    else -> null
}

private fun safeEndpoint(value: String, allowLoopback: Boolean): Boolean = runCatching {
    val uri = java.net.URI(value.trim())
    val loopback = uri.host?.lowercase() in setOf("localhost", "127.0.0.1", "[::1]", "::1")
    uri.host != null && uri.rawUserInfo == null && uri.rawQuery == null && uri.rawFragment == null &&
        (uri.scheme.equals("https", true) || (allowLoopback && loopback && uri.scheme.equals("http", true)))
}.getOrDefault(false)
