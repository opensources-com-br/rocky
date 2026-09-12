package dev.rocky.core.agent

enum class AgentTone {
    Direct,
    Energetic,
    Analytical,
    Ironic,
}

data class AgentConfiguration(
    val conversation: List<ConversationTurn> = emptyList(),
    val name: String = "Rocky",
    val tone: AgentTone = AgentTone.Direct,
    val interventionsPerTenMinutes: Int = 3,
    val language: dev.rocky.core.locale.RockyLanguage = dev.rocky.core.locale.RockyLanguage.PortugueseBrazil,
)

data class ConversationTurn(val question: String, val answer: String)
