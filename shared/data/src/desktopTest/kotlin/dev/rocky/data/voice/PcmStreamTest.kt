package dev.rocky.data.voice

import dev.rocky.core.voice.PcmPlayback
import java.io.ByteArrayInputStream
import kotlin.test.*

class PcmStreamTest {
    private class Player : PcmPlayback {
        val output = mutableListOf<Byte>()
        var finished = false
        override fun start(sampleRate: Int, volumePercent: Int) {}
        override fun write(bytes: ByteArray, count: Int) { output += bytes.take(count) }
        override fun finish() { finished = true }
        override fun cancel() {}
    }
    @Test fun joinsOddNetworkChunksWithoutLosingSamples() {
        val bytes = ByteArray(20) { it.toByte() }
        val input = object : ByteArrayInputStream(bytes) {
            override fun read(b: ByteArray, off: Int, len: Int) = super.read(b, off, minOf(len, 3))
        }
        val player = Player()
        var first = 0
        playPcm(input, player, {}, { first++ })
        assertEquals(bytes.toList(), player.output)
        assertEquals(1, first)
        assertTrue(player.finished)
    }
}
