package dev.rocky.core.agent

enum class AgentTone {
    Direct,
    Energetic,
    Analytical,
    Ironic,
}

data class AgentConfiguration(
    val name: String = "Rocky",
    val tone: AgentTone = AgentTone.Direct,
    val interventionsPerTenMinutes: Int = 3,
)
