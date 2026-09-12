package dev.rocky.platform.desktop

import dev.rocky.core.youtube.YouTubeConfiguration
import java.util.prefs.Preferences

object YouTubeDesktopPreferences {
    private val delegate by lazy {
        SecureYouTubePreferences(
            Preferences.userRoot().node("dev/rocky/youtube"),
            desktopSecretStore("youtube"),
        )
    }
    var configuration: YouTubeConfiguration
        get() = delegate.configuration
        set(value) { delegate.configuration = value }
    fun clear() = delegate.clear()
}

internal class SecureYouTubePreferences(
    private val preferences: Preferences,
    private val secrets: SecretStore,
) {
    var configuration: YouTubeConfiguration
        get() = YouTubeConfiguration(
            clientId = preferences.get("clientId", ""),
            clientSecret = runCatching { secrets.read().orEmpty() }.getOrDefault(""),
            redirectUri = preferences.get("redirectUri", YouTubeConfiguration().redirectUri),
        )
        set(value) {
            if (value.clientSecret.isBlank()) secrets.delete() else secrets.write(value.clientSecret.trim())
            preferences.put("clientId", value.clientId.trim())
            preferences.put("redirectUri", value.redirectUri.trim())
            preferences.flush()
        }

    fun clear() {
        secrets.delete()
        preferences.clear()
        preferences.flush()
    }
}
