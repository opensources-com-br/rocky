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
    private var carbon: Carbon? = null
    private var handlerRef: Pointer? = null
    private val refs = mutableListOf<Pointer>()
    private var handler: Carbon.Handler? = null

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
        } else if (Platform.isMac()) {
            try {
                val api = Native.load("Carbon", Carbon::class.java)
                carbon = api
                handler = Carbon.Handler { _, event, _ ->
                    val id = HotKeyId()
                    val result = api.GetEventParameter(event, 0x2d2d2d2d, 0x686b6964, null, id.size(), null, id)
                    if (result == 0) { id.read(); dispatch(id.id - 1) }
                    0
                }
                val spec = EventSpec().apply { eventClass = 0x6b657962; kind = 5; write() }
                val reference = PointerByReference()
                check(api.InstallEventHandler(api.GetEventDispatcherTarget(), handler!!, 1, spec, null, reference) == 0)
                handlerRef = reference.value
                val codes = listOf(122, 120, 99, 118, 96, 97, 98, 100, 101, 109, 103, 111)
                configuration.keys.forEachIndexed { index, key ->
                    val id = HotKeyValue().apply { signature = 0x726f636b; this.id = index + 1; write() }
                    val ref = PointerByReference()
                    check(api.RegisterEventHotKey(codes[key - 1], 4096 or 512, id, api.GetEventDispatcherTarget(), 0, ref) == 0)
                    refs.add(ref.value)
                }
                onStatus(true)
            } catch (_: Exception) { releaseMac(); onStatus(false) }
            catch (_: LinkageError) { releaseMac(); onStatus(false) }
        } else onStatus(false)
    }

    private fun dispatch(action: Int) {
        EventQueue.invokeLater { if (running && action in 0..2) onAction(action) }
    }

    private fun releaseMac() {
        carbon?.let { api ->
            refs.forEach { api.UnregisterEventHotKey(it) }; refs.clear()
            handlerRef?.let { api.RemoveEventHandler(it) }; handlerRef = null
        }
        handler = null
    }

    override fun close() {
        running = false
        worker?.join(500)
        releaseMac()
    }

    @Structure.FieldOrder("signature", "id")
    open class HotKeyId : Structure() { @JvmField var signature = 0; @JvmField var id = 0 }
    class HotKeyValue : HotKeyId(), Structure.ByValue
    @Structure.FieldOrder("eventClass", "kind")
    class EventSpec : Structure() { @JvmField var eventClass = 0; @JvmField var kind = 0 }
    interface Carbon : Library {
        fun interface Handler : Callback { fun invoke(next: Pointer?, event: Pointer?, user: Pointer?): Int }
        fun GetEventDispatcherTarget(): Pointer
        fun InstallEventHandler(target: Pointer, handler: Handler, count: Int, types: EventSpec, user: Pointer?, ref: PointerByReference): Int
        fun RegisterEventHotKey(key: Int, modifiers: Int, id: HotKeyValue, target: Pointer, options: Int, ref: PointerByReference): Int
        fun UnregisterEventHotKey(ref: Pointer): Int
        fun RemoveEventHandler(ref: Pointer): Int
        fun GetEventParameter(event: Pointer?, name: Int, type: Int, actualType: Pointer?, size: Int, actualSize: Pointer?, data: HotKeyId): Int
    }
}
