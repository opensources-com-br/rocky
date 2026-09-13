package dev.rocky.platform.desktop

import dev.rocky.core.facebook.FacebookConfiguration
import java.util.UUID
import java.util.prefs.Preferences
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class FacebookDesktopPreferencesTest {
    @Test fun storesTheAppSecretOutsidePreferences() {
        val preferences = Preferences.userRoot().node("dev/rocky/test/facebook-${UUID.randomUUID()}")
        val secrets = MemorySecretStore()
        try {
            val secure = SecureFacebookPreferences(preferences, secrets)
            secure.configuration = FacebookConfiguration(" app ", " secret ")
            assertEquals("app", secure.configuration.appId)
            assertEquals("secret", secure.configuration.appSecret)
            assertFalse(preferences.keys().contains("appSecret"))
            secure.clear()
            assertEquals("", secure.configuration.appSecret)
        } finally {
            preferences.removeNode()
        }
    }

    private class MemorySecretStore : SecretStore {
        private var value: String? = null
        override fun read() = value
        override fun write(value: String) { this.value = value }
        override fun delete() { value = null }
    }
}
