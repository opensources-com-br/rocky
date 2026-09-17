package dev.rocky.platform.desktop

import java.util.prefs.Preferences

object FirstUseDesktopPreferences {
    private val delegate by lazy {
        FirstUsePreferences(Preferences.userRoot().node("dev/rocky/onboarding"))
    }

    var completed: Boolean
        get() = delegate.completed
        set(value) { delegate.completed = value }
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
