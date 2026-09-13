package dev.rocky.data.voice

import dev.rocky.core.voice.PcmPlayback
import java.io.InputStream

internal fun playPcm(input: InputStream, player: PcmPlayback, checkActive: () -> Unit, onFirstAudio: () -> Unit) {
    val buffer = ByteArray(4096)
    var pending = 0
    var total = 0L
    while (true) {
        checkActive()
        val count = input.read(buffer, pending, buffer.size - pending)
        if (count < 0) break
        val available = count + pending
        val aligned = available - available % 2
        if (aligned > 0) {
            if (total == 0L) onFirstAudio()
            total += aligned
            require(total <= 24_000 * 2 * 120) { "Resposta de áudio excedeu o limite." }
            player.write(buffer, aligned)
        }
        pending = available % 2
        if (pending == 1) buffer[0] = buffer[aligned]
    }
    require(total > 0 && pending == 0) { "ElevenLabs retornou áudio vazio ou incompleto." }
    checkActive()
    player.finish()
}
