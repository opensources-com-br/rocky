package dev.rocky.platform.desktop

import dev.rocky.core.facebook.FacebookConfiguration
import java.util.prefs.Preferences

object FacebookDesktopPreferences {
    private val delegate by lazy {
        SecureFacebookPreferences(
            Preferences.userRoot().node("dev/rocky/facebook"),
            desktopSecretStore("facebook"),
        )
    }
    var configuration: FacebookConfiguration
        get() = delegate.configuration
        set(value) { delegate.configuration = value }
    fun clear() = delegate.clear()
}

internal class SecureFacebookPreferences(
    private val preferences: Preferences,
    private val secrets: SecretStore,
) {
    var configuration: FacebookConfiguration
        get() = FacebookConfiguration(
            appId = preferences.get("appId", ""),
            appSecret = runCatching { secrets.read().orEmpty() }.getOrDefault(""),
            redirectUri = preferences.get("redirectUri", FacebookConfiguration().redirectUri),
        )
        set(value) {
            if (value.appSecret.isBlank()) secrets.delete() else secrets.write(value.appSecret.trim())
            preferences.put("appId", value.appId.trim())
            preferences.put("redirectUri", value.redirectUri.trim())
            preferences.flush()
        }

    fun clear() {
        secrets.delete()
        preferences.clear()
        preferences.flush()
    }
}
