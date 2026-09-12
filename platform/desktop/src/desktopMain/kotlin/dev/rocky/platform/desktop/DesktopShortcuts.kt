package dev.rocky.platform.desktop

import com.sun.jna.*
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser
import java.awt.EventQueue
import java.util.prefs.Preferences

data class ShortcutConfiguration(val microphone: Int = 8, val silence: Int = 9, val window: Int = 10) {
    val keys get() = listOf(microphone, silence, window)
    val valid get() = keys.all { it in 1..12 } && keys.distinct().size == 3
}

object ShortcutPreferences {
    private val prefs = Preferences.userRoot().node("dev/rocky/shortcuts")
    var configuration: ShortcutConfiguration
        get() = ShortcutConfiguration(prefs.getInt("microphone", 8), prefs.getInt("silence", 9), prefs.getInt("window", 10))
            .takeIf { it.valid } ?: ShortcutConfiguration()
        set(value) {
            require(value.valid)
            prefs.putInt("microphone", value.microphone)
            prefs.putInt("silence", value.silence)
            prefs.putInt("window", value.window)
        }
}

