package dev.rocky.ui.window

internal enum class MainSection(val label: String) {
    Conversation("Conversa"),
    Support("Apoios"),
    Notes("Notas"),
    Ideas("Ideias"),
    Pulse("Pulso"),
}

internal enum class SettingsSection(val label: String) {
    Agent("Agente"),
    Ai("IA"),
    Voice("Voz"),
    Platforms("Plataformas"),
}

internal data class PlatformStatus(
    val name: String,
    val account: String,
    val audience: String,
    val messagesPerMinute: Int,
    val colorKey: PlatformColor,
    val enabled: Boolean = true,
)

internal enum class PlatformColor {
    Twitch,
    Kick,
    YouTube,
    Offline,
}

internal val samplePlatforms = listOf(
    PlatformStatus("Twitch", "@seucanal", "820", 26, PlatformColor.Twitch),
    PlatformStatus("Kick", "@seucanal", "214", 9, PlatformColor.Kick),
    PlatformStatus("YouTube", "Canal principal", "187", 6, PlatformColor.YouTube),
    PlatformStatus("Facebook", "Página não conectada", "0", 0, PlatformColor.Offline, enabled = false),
)
