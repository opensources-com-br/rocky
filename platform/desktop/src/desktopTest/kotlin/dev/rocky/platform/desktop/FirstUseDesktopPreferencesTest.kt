package dev.rocky.platform.desktop
import java.util.UUID
import java.util.prefs.Preferences
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FirstUseDesktopPreferencesTest {
    @Test fun persistsCompletedOnboarding() {
        val preferences = Preferences.userRoot().node("dev/rocky-tests/${UUID.randomUUID()}")
        try {
            val store = FirstUsePreferences(preferences)
            assertFalse(store.completed)
            store.completed = true
            assertTrue(FirstUsePreferences(preferences).completed)
        } finally {
            preferences.removeNode(); preferences.flush()
        }
    }
}
