package dev.rocky.platform.desktop

import dev.rocky.core.voice.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

internal class WhisperTranscriber {
    private val transcriptionLock = Any()
    @Volatile private var transcriptionProcess: Process? = null
    @Volatile private var transcriptionCancelled = false
    fun begin() { synchronized(transcriptionLock) { transcriptionCancelled = false } }
    fun cancel() {
        val process = synchronized(transcriptionLock) {
            transcriptionCancelled = true
            transcriptionProcess.also { transcriptionProcess = null }
        }
        process?.destroyForcibly()
    }
}
