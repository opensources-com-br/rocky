package dev.rocky.platform.desktop

import java.io.ByteArrayOutputStream
import javax.sound.sampled.*

internal class DesktopMicrophone : dev.rocky.core.voice.AudioCapture {
    private val lock = Any()
    private var line: TargetDataLine? = null
    private var worker: Thread? = null
    private var audio = ByteArrayOutputStream()
    @Volatile private var inputLevel = 0f
    override fun level() = inputLevel
    private fun record(active: TargetDataLine, output: ByteArrayOutputStream) {
        val buffer = ByteArray(640)
        while (active.isOpen && output.size() < 16_000 * 2 * 60) {
            val count = runCatching { active.read(buffer, 0, buffer.size) }.getOrDefault(-1)
            if (count <= 0) break
            output.write(buffer, 0, count)
            inputLevel = DesktopVoiceService.pcmLevel(buffer, count)
        }
    }

    override fun start(microphoneId: String?): Unit = synchronized(lock) {
        check(line == null) { "Microfone já ativo" }
        check(!Thread.currentThread().isInterrupted)
        val format = AudioFormat(16_000f, 16, 1, true, false)
        val info = DataLine.Info(TargetDataLine::class.java, format)
        val mixer = microphoneId?.let { id -> AudioSystem.getMixerInfo().firstOrNull { it.name == id }
            ?: error("Microfone selecionado indisponível") }?.let(AudioSystem::getMixer)
        val active = (mixer?.getLine(info) ?: AudioSystem.getLine(info)) as TargetDataLine
        try { active.open(format); active.start() } catch (error: Exception) { active.close(); throw error }
        audio = ByteArrayOutputStream(); inputLevel = 0f; line = active
        val output = audio
        worker = Thread({ record(active, output) }, "rocky-microphone-capture").apply { isDaemon = true; start() }
    }

    override fun finish(): ByteArray = synchronized(lock) {
        val active = line ?: error("Microfone inativo")
        active.stop(); active.close()
        worker?.join(1000)
        line = null; worker = null; inputLevel = 0f
        audio.toByteArray().also { audio.reset() }
    }
    override fun cancel() { synchronized(lock) { if (line != null) finish() } }
}
