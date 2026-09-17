package dev.rocky.platform.desktop

import java.awt.Rectangle
import kotlin.test.*

class DesktopWindowPreferencesTest {
    @Test fun keepsAResizedCaptureAtTheMinimumVisibleSize() {
        val restored = fitWindow(Rectangle(10, 10, 100, 50), listOf(Rectangle(0, 0, 1920, 1080)))

        assertEquals(340, restored.width)
        assertEquals(180, restored.height)
    }

    @Test fun fitsAWindowAfterItsMonitorIsRemoved() {
        val screen = Rectangle(0, 0, 1280, 720)
        val restored = fitWindow(Rectangle(2000, 900, 462, 820), listOf(screen))
        assertTrue(screen.contains(restored))
        assertEquals(462, restored.width)
        assertEquals(720, restored.height)
    }
    @Test fun preservesBoundsOnANegativeCoordinateMonitor() {
        val saved = Rectangle(-900, 80, 462, 600)
        assertEquals(saved, fitWindow(saved, listOf(Rectangle(-1280, 0, 1280, 800))))
    }
    @Test fun rejectsDuplicateOrInvalidShortcutKeys() {
        assertTrue(ShortcutConfiguration().valid)
        assertFalse(ShortcutConfiguration(8, 8, 10).valid)
        assertFalse(ShortcutConfiguration(0, 9, 10).valid)
    }
}
