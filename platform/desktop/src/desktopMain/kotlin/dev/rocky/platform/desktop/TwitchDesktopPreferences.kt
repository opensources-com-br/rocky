package dev.rocky.platform.desktop

import java.util.prefs.Preferences

object TwitchDesktopPreferences {
    private val preferences = Preferences.userRoot().node("dev/rocky/twitch")

    var clientId: String
        get() = preferences.get(CLIENT_ID_KEY, "")
        set(value) {
            preferences.put(CLIENT_ID_KEY, value.trim())
        }

    private const val CLIENT_ID_KEY = "clientId"
}
