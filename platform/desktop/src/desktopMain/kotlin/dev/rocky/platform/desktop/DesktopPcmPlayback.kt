package dev.rocky.platform.desktop

import dev.rocky.core.voice.PcmPlayback
import javax.sound.sampled.*

class DesktopPcmPlayback : PcmPlayback {
    private val lock = Any()
    @Volatile private var line: SourceDataLine? = null
    private var volume = 1f
    override fun start(sampleRate: Int, volumePercent: Int) = synchronized(lock) {
        check(!Thread.currentThread().isInterrupted)
        val format = AudioFormat(sampleRate.toFloat(), 16, 1, true, false)
        val opened = AudioSystem.getSourceDataLine(format)
        try { opened.open(format, sampleRate / 5 * 2); opened.start() }
        catch (error: Exception) { opened.close(); throw error }
        volume = volumePercent.coerceIn(0, 100) / 100f
        line = opened
    }
    override fun cancel() {
        val active = synchronized(lock) { line.also { line = null } }
        active?.stop(); active?.flush(); active?.close()
    }
    override fun finish() { line?.drain(); cancel() }
    override fun write(bytes: ByteArray, count: Int) {
        require(count % 2 == 0)
        val active = line ?: throw InterruptedException("Reprodução cancelada")
        val scaled = bytes.copyOf(count)
        for (i in 0 until count step 2) {
            val sample = (((scaled[i + 1].toInt() shl 8) or (scaled[i].toInt() and 255)).toShort() * volume).toInt()
            scaled[i] = sample.toByte(); scaled[i + 1] = (sample shr 8).toByte()
        }
        check(active.write(scaled, 0, count) == count) { "Reprodução interrompida" }
    }
}
