package dev.rocky.platform.desktop

import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.agent.AgentTone
import java.util.prefs.Preferences

object AgentDesktopPreferences {
    private val preferences = Preferences.userRoot().node("dev/rocky/agent")

    var configuration: AgentConfiguration
        get() = AgentConfiguration(
            name = preferences.get(NAME_KEY, "Rocky"),
            tone = runCatching {
                AgentTone.valueOf(preferences.get(TONE_KEY, AgentTone.Direct.name))
            }.getOrDefault(AgentTone.Direct),
            interventionsPerTenMinutes = preferences.getInt(FREQUENCY_KEY, 3).coerceIn(1, 9),
        )
        set(value) {
            preferences.put(NAME_KEY, value.name.trim().ifBlank { "Rocky" })
            preferences.put(TONE_KEY, value.tone.name)
            preferences.putInt(FREQUENCY_KEY, value.interventionsPerTenMinutes.coerceIn(1, 9))
        }

    private const val NAME_KEY = "name"
    private const val TONE_KEY = "tone"
    private const val FREQUENCY_KEY = "interventionsPerTenMinutes"
}
