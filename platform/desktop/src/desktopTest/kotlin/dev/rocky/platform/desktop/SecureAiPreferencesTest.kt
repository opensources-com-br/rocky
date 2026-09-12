package dev.rocky.platform.desktop

import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import java.util.UUID
import java.util.prefs.Preferences
import kotlin.test.*

class SecureAiPreferencesTest {
    private fun withStore(test: (Preferences, FakeSecrets) -> Unit) {
        val prefs = Preferences.userRoot().node("dev/rocky-tests/${UUID.randomUUID()}")
        try { test(prefs, FakeSecrets()) } finally { prefs.removeNode(); prefs.flush() }
    }
    @Test fun migratesLegacyKeyAndDeletesPlaintext() = withStore { prefs, secrets ->
        prefs.put("provider", "OpenAI"); prefs.put("endpoint", "https://api.openai.com")
        prefs.put("apiKey", "synthetic")
        assertEquals("synthetic", SecureAiPreferences(prefs, secrets).configuration.apiKey)
        assertNull(prefs.get("apiKey", null))
        assertEquals("synthetic", SecureAiPreferences(prefs, secrets).configuration.apiKey)
    }
    @Test fun unavailableVaultNeverRetainsPlaintext() = withStore { prefs, secrets ->
        prefs.put("apiKey", "synthetic"); secrets.fail = true
        val store = SecureAiPreferences(prefs, secrets)
        assertEquals("synthetic", store.configuration.apiKey)
        assertNotNull(store.storageNotice)
        assertNull(prefs.get("apiKey", null))
    }
    @Test fun neverRestoresKeyForDifferentOriginAndSupportsRemoval() = withStore { prefs, secrets ->
        val config = AiProviderConfiguration(AiProviderKind.OpenAI, "https://api.openai.com", "test", "synthetic")
        SecureAiPreferences(prefs, secrets).configuration = config
        prefs.put("endpoint", "https://another.example")
        assertEquals("", SecureAiPreferences(prefs, secrets).configuration.apiKey)
        SecureAiPreferences(prefs, secrets).configuration = config.copy(apiKey = "")
        assertNull(secrets.value)
    }
    private class FakeSecrets : SecretStore {
        var value: String? = null
        var fail = false
        override fun read() = value
        override fun write(value: String) { check(!fail); this.value = value }
        override fun delete() { value = null }
    }
}
