package dev.rocky.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DesktopWindowLifecycleTest {
    @Test fun trayAppStartsHidden() {
        assertFalse(DesktopWindowLifecycle(usesTray = true).mainVisible)
    }

    @Test fun fallbackAppStartsVisible() {
        assertTrue(DesktopWindowLifecycle(usesTray = false).mainVisible)
    }

    @Test fun closingTrayWindowKeepsApplicationRunning() {
        val lifecycle = DesktopWindowLifecycle(usesTray = true).apply { showMain() }
        assertFalse(lifecycle.closeMain())
        assertFalse(lifecycle.mainVisible)
    }

    @Test fun trayActionRestoresClosedWindow() {
        val lifecycle = DesktopWindowLifecycle(usesTray = true)
        lifecycle.showMain()
        assertTrue(lifecycle.mainVisible)
    }

    @Test fun closingFallbackWindowRequestsQuit() {
        assertTrue(DesktopWindowLifecycle(usesTray = false).closeMain())
    }

    @Test fun repeatedSettingsActionsReuseOneRequestChannel() {
        val lifecycle = DesktopWindowLifecycle(usesTray = true)
        lifecycle.requestSettings(); lifecycle.requestSettings()
        assertEquals(2, lifecycle.settingsRevision)
    }
}
