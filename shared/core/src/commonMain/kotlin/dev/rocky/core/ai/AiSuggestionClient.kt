package dev.rocky.core.ai

import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.live.ChatMessage

enum class AiProviderKind {
    Ollama,
    OpenAI,
}

data class AiProviderConfiguration(
    val provider: AiProviderKind,
    val endpoint: String,
    val model: String,
    val apiKey: String = "",
)

data class AiConnectionResult(
    val successful: Boolean,
    val message: String,
)

data class AiGeneratedSuggestion(
    val text: String,
    val sourceMessageIds: Set<String>,
)

interface AiSuggestionClient : AutoCloseable {
    fun testConnection(configuration: AiProviderConfiguration): AiConnectionResult

    fun generateSuggestion(
        configuration: AiProviderConfiguration,
        messages: List<ChatMessage>,
        streamerRequest: String? = null,
        agent: AgentConfiguration = AgentConfiguration(),
    ): AiGeneratedSuggestion?

    override fun close()
}
