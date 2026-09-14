package dev.rocky.platform.desktop

import dev.rocky.core.tiktok.TikTokConfiguration
import java.util.prefs.Preferences

object TikTokDesktopPreferences {
    private val delegate by lazy { TikTokPreferences(Preferences.userRoot().node("dev/rocky/tiktok")) }

    var configuration: TikTokConfiguration
        get() = delegate.configuration
        set(value) { delegate.configuration = value }

    fun clear() = delegate.clear()
}

internal class TikTokPreferences(private val preferences: Preferences) {
    var configuration: TikTokConfiguration
        get() = TikTokConfiguration(preferences.get("username", ""))
        set(value) {
            preferences.put("username", value.username.trim().removePrefix("@").trim())
            preferences.flush()
        }

    fun clear() {
        preferences.clear()
        preferences.flush()
    }
}
