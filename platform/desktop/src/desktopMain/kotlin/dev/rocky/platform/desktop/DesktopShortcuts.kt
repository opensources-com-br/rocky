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

/** Registers only three selected chords; does not install a keyboard logger. */
class DesktopShortcuts(
    private val configuration: ShortcutConfiguration,
    private val onAction: (Int) -> Unit,
    private val onStatus: (Boolean) -> Unit,
) : AutoCloseable {
    @Volatile private var running = true
    private var worker: Thread? = null

    fun start() {
        require(configuration.valid)
        if (Platform.isWindows()) {
            worker = Thread({
                val api = User32.INSTANCE
                val registered = mutableListOf<Int>()
                try {
                    configuration.keys.forEachIndexed { index, key ->
                        check(api.RegisterHotKey(null, index + 1, 0x0002 or 0x0004 or 0x4000, 0x70 + key - 1))
                        registered.add(index + 1)
                    }
                    EventQueue.invokeLater { if (running) onStatus(true) }
                    val message = WinUser.MSG()
                    while (running) {
                        while (api.PeekMessage(message, null, 0, 0, 1)) {
                            if (message.message == 0x0312) dispatch(message.wParam.toInt() - 1)
                        }
                        Thread.sleep(25)
                    }
                } catch (_: Exception) {
                    EventQueue.invokeLater { if (running) onStatus(false) }
                } finally {
                    registered.forEach { api.UnregisterHotKey(null, it) }
                }
            }, "rocky-shortcuts").apply { isDaemon = true; start() }
        } else onStatus(false)
    }

    private fun dispatch(action: Int) {
        EventQueue.invokeLater { if (running && action in 0..2) onAction(action) }
    }

    override fun close() {
        running = false
        worker?.join(500)
    }

}
