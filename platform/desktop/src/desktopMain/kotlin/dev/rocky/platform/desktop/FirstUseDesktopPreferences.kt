package dev.rocky.platform.desktop

import java.util.prefs.Preferences

object FirstUseDesktopPreferences {
    private val preferences = Preferences.userRoot().node("dev/rocky/onboarding")

    var completed: Boolean
        get() = preferences.getBoolean(COMPLETED_KEY, false)
        set(value) {
            preferences.putBoolean(COMPLETED_KEY, value)
        }

    private const val COMPLETED_KEY = "completed"
}

internal class FirstUsePreferences(private val preferences: Preferences) {
    var completed: Boolean
        get() = preferences.getBoolean(COMPLETED_KEY, false)
        set(value) {
            preferences.putBoolean(COMPLETED_KEY, value)
            preferences.flush()
        }

    private companion object { const val COMPLETED_KEY = "completed" }
}
