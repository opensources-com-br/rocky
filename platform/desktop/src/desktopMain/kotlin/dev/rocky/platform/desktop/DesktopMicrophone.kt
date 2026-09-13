package dev.rocky.platform.desktop

import java.io.ByteArrayOutputStream
import javax.sound.sampled.*

internal class DesktopMicrophone : dev.rocky.core.voice.AudioCapture {
    private val lock = Any()
    private val samplesLock = Any()
    private var line: TargetDataLine? = null
    private var worker: Thread? = null
    private var audio = ByteArrayOutputStream()
    @Volatile private var inputLevel = 0f
    override fun level() = inputLevel
    private fun record(active: TargetDataLine) {
        val buffer = ByteArray(640)
        while (active.isOpen) {
            val count = runCatching { active.read(buffer, 0, buffer.size) }.getOrDefault(-1)
            if (count <= 0) break
            synchronized(samplesLock) {
                if (audio.size() + count > 16_000 * 2 * 60) {
                    val recent = audio.toByteArray().takeLast(16_000 * 2 * 30).toByteArray()
                    audio.reset(); audio.write(recent)
                }
                audio.write(buffer, 0, count)
            }
            inputLevel = DesktopVoiceService.pcmLevel(buffer, count)
        }
    }

    override fun start(microphoneId: String?): Unit = synchronized(lock) {
        if (line != null) return@synchronized
        check(!Thread.currentThread().isInterrupted)
        val format = AudioFormat(16_000f, 16, 1, true, false)
        val info = DataLine.Info(TargetDataLine::class.java, format)
        val mixer = microphoneId?.let { id -> AudioSystem.getMixerInfo().firstOrNull { it.name == id }
            ?: error("Microfone selecionado indisponível") }?.let(AudioSystem::getMixer)
        val active = (mixer?.getLine(info) ?: AudioSystem.getLine(info)) as TargetDataLine
        try { active.open(format); active.start() } catch (error: Exception) { active.close(); throw error }
        audio = ByteArrayOutputStream(); inputLevel = 0f; line = active
        worker = Thread({ record(active) }, "rocky-microphone-capture").apply { isDaemon = true; start() }
    }

    override fun finish(): ByteArray = synchronized(lock) {
        val active = line ?: error("Microfone inativo")
        active.stop(); active.close()
        worker?.join(1000)
        line = null; worker = null; inputLevel = 0f
        segment()
    }
    fun segment(): ByteArray = synchronized(samplesLock) { audio.toByteArray().also { audio.reset() } }
    fun hasBufferedSpeech(threshold: Float): Boolean = synchronized(samplesLock) {
        val bytes = audio.toByteArray()
        var voiced = 0
        for (offset in 0 until bytes.size - 639 step 640) {
            val level = DesktopVoiceService.pcmLevel(bytes.copyOfRange(offset, offset + 640), 640)
            voiced = if (level >= threshold) voiced + 20 else 0
            if (voiced >= 220) return@synchronized true
        }
        false
    }
    override fun cancel() { synchronized(lock) { if (line != null) finish() } }
}
