package dev.rocky.platform.desktop

import dev.rocky.core.voice.VoiceOutputConfiguration
import java.util.concurrent.TimeUnit

internal class SystemSpeechOutput {
    private val lock = Any()
    private var process: Process? = null
    fun cancel() {
        val active = synchronized(lock) { process.also { process = null } }
        active?.destroy()
        if (active != null && !active.waitFor(300, TimeUnit.MILLISECONDS)) active.destroyForcibly()
    }
}
