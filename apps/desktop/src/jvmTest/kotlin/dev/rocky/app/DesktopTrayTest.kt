package dev.rocky.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DesktopTrayTest {
    @Test fun usesTrayOnWindows() {
        assertTrue(shouldUseTray("Windows 11", systemTraySupported = true))
    }

    @Test fun keepsTrayOnMacOs() {
        assertTrue(shouldUseTray("Mac OS X", systemTraySupported = true))
    }

    @Test fun keepsWindowLifecycleOnLinux() {
        assertFalse(shouldUseTray("Linux", systemTraySupported = true))
    }

    @Test fun fallsBackWhenSystemTrayIsUnavailable() {
        assertFalse(shouldUseTray("Windows 11", systemTraySupported = false))
    }

    @Test fun usesCompactWindowsMenuLabel() {
        assertEquals("Settings", traySettingsLabel("Windows 11"))
    }

    @Test fun preservesMacOsMenuWidth() {
        assertTrue(traySettingsLabel("Mac OS X").length > "Settings".length)
    }
}
