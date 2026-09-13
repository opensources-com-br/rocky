package dev.rocky.platform.desktop

import dev.rocky.core.voice.VoiceOutputConfiguration
import java.util.concurrent.TimeUnit

internal class SystemSpeechOutput : dev.rocky.core.voice.SpeechOutput {
    private val lock = Any()
    private var process: Process? = null
    override fun cancel() {
        val active = synchronized(lock) { process.also { process = null } }
        active?.destroy()
        if (active != null && !active.waitFor(300, TimeUnit.MILLISECONDS)) active.destroyForcibly()
    }
    override fun speak(text: String, configuration: VoiceOutputConfiguration) {
        require(text.isNotBlank())
        val command = when {
            System.getProperty("os.name").lowercase().contains("mac") -> DesktopVoiceService.macSpeechCommand(text, configuration)
            System.getProperty("os.name").lowercase().contains("win") -> DesktopVoiceService.windowsSpeechCommand(text, configuration)
            else -> error("Voz do sistema indisponível")
        }
        val active = synchronized(lock) {
            check(!Thread.currentThread().isInterrupted) { "Leitura cancelada" }
            ProcessBuilder(command).start().also { process = it }
        }
        try {
            val completed = DesktopVoiceService.waitForProcess(active, 5, TimeUnit.MINUTES)
            check(completed && active.exitValue() == 0) { "Falha na voz do sistema" }
        } finally {
            synchronized(lock) { if (process === active) process = null }
            if (active.isAlive) active.destroyForcibly()
        }
    }
}
