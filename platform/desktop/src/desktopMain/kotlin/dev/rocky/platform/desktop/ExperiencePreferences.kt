package dev.rocky.platform.desktop

import dev.rocky.core.agent.InterventionProfile
import dev.rocky.core.live.ChatFilterConfiguration
import java.util.prefs.Preferences

object ExperiencePreferences {
    private val prefs = Preferences.userRoot().node("dev/rocky/experience")
    var checkUpdatesOnStart: Boolean
        get() = prefs.getBoolean("checkUpdatesOnStart", true)
        set(value) { prefs.putBoolean("checkUpdatesOnStart", value) }
    var profile: InterventionProfile
        get() = runCatching { InterventionProfile.valueOf(prefs.get("profile",
            if (AiDesktopPreferences.automaticAnalysis) "Discreet" else "OnDemand")) }.getOrDefault(InterventionProfile.OnDemand)
        set(value) { prefs.put("profile", value.name) }
    var filters: ChatFilterConfiguration
        get() = ChatFilterConfiguration(prefs.getBoolean("commands", true), prefs.getBoolean("repetitions", true),
            prefs.get("bots", "nightbot,streamelements,streamlabs,moobot").split(',').map { it.trim().lowercase() }.filter { it.isNotBlank() }.toSet())
        set(value) {
            prefs.putBoolean("commands", value.commands)
            prefs.putBoolean("repetitions", value.repetitions)
            prefs.put("bots", value.bots.joinToString(",").take(2000))
        }
}
