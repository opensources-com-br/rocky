package dev.rocky.platform.desktop

import java.io.ByteArrayOutputStream
import javax.sound.sampled.*

internal class DesktopMicrophone {
    private val lock = Any()
    private var line: TargetDataLine? = null
    private var worker: Thread? = null
    private var audio = ByteArrayOutputStream()
    @Volatile private var inputLevel = 0f
    fun level() = inputLevel

    fun finish(): ByteArray = synchronized(lock) {
        val active = line ?: error("Microfone inativo")
        active.stop(); active.close()
        worker?.join(1000)
        line = null; worker = null; inputLevel = 0f
        audio.toByteArray().also { audio.reset() }
    }
    fun cancel() { synchronized(lock) { if (line != null) finish() } }
}
