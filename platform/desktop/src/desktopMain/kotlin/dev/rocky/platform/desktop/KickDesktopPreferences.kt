package dev.rocky.platform.desktop

import dev.rocky.core.kick.KickConfiguration
import java.util.prefs.Preferences

object KickDesktopPreferences {
    private val delegate by lazy {
        SecureKickPreferences(
            Preferences.userRoot().node("dev/rocky/kick"),
            desktopSecretStore("kick"),
        )
    }
    var configuration: KickConfiguration
        get() = delegate.configuration
        set(value) { delegate.configuration = value }
    fun clear() = delegate.clear()
}

internal class SecureKickPreferences(
    private val preferences: Preferences,
    private val secrets: SecretStore,
) {
    var configuration: KickConfiguration
        get() = KickConfiguration(
            clientId = preferences.get("clientId", ""),
            clientSecret = runCatching { secrets.read().orEmpty() }.getOrDefault(""),
            redirectUri = preferences.get("redirectUri", KickConfiguration().redirectUri),
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
