package dev.rocky.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DesktopWindowLifecycleTest {
    @Test fun trayAppStartsHidden() {
        assertFalse(DesktopWindowLifecycle(usesTray = true).mainVisible)
    }
}
