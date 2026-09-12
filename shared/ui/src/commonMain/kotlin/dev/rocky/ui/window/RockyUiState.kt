package dev.rocky.ui.window

internal enum class MainSection(val label: String) {
    Conversation("Conversa"),
    Support("Superchats"),
    Notes("Notas"),
    Ideas("Ideias"),
    Pulse("Pulso"),
}

internal enum class SettingsSection(val label: String) {
    Agent("Agente"),
    Ai("IA"),
    Voice("Voz"),
    Platforms("Plataformas"),
    Data("Dados"),
}

internal data class PlatformStatus(
    val name: String,
    val account: String,
    val audience: String,
    val messagesPerMinute: Int,
    val colorKey: PlatformColor,
    val enabled: Boolean = true,
    val connected: Boolean = false,
)

internal enum class PlatformColor {
    Twitch,
    Kick,
    YouTube,
    Offline,
}
