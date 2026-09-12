package dev.rocky.platform.desktop

import dev.rocky.core.kick.KickConfiguration
import java.util.UUID
import java.util.prefs.Preferences
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KickDesktopPreferencesTest {
    @Test fun storesSecretOutsidePreferencesAndClearsIt() {
        val preferences = Preferences.userRoot().node("dev/rocky-tests/${UUID.randomUUID()}")
        val secrets = FakeSecrets()
        try {
            val store = SecureKickPreferences(preferences, secrets)
            store.configuration = KickConfiguration(" client ", " secret ")
            assertNull(preferences.get("clientSecret", null))
            assertEquals("secret", store.configuration.clientSecret)
            store.clear()
            assertNull(secrets.value)
        } finally {
            preferences.removeNode()
            preferences.flush()
        }
    }

    private class FakeSecrets : SecretStore {
        var value: String? = null
        override fun read() = value
        override fun write(value: String) { this.value = value }
        override fun delete() { value = null }
    }
}
