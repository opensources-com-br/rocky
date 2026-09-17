package dev.rocky.app

import kotlin.test.Test
import kotlin.test.assertTrue

class DesktopTrayTest {
    @Test fun usesTrayOnWindows() {
        assertTrue(shouldUseTray("Windows 11", systemTraySupported = true))
    }
}
