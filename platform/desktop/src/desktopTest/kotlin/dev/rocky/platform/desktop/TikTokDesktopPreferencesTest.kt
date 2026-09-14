package dev.rocky.platform.desktop

import dev.rocky.core.tiktok.TikTokConfiguration
import java.util.UUID
import java.util.prefs.Preferences
import kotlin.test.Test
import kotlin.test.assertEquals

class TikTokDesktopPreferencesTest {
    @Test
    fun normalizesAndClearsTheSavedUsername() {
        val preferences = Preferences.userRoot().node("dev/rocky-tests/${UUID.randomUUID()}")
        try {
            val store = TikTokPreferences(preferences)
            store.configuration = TikTokConfiguration(" @rocky_live ")
            assertEquals("rocky_live", store.configuration.username)
            store.clear()
            assertEquals("", store.configuration.username)
        } finally {
            preferences.removeNode()
            preferences.flush()
        }
    }
}
