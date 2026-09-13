package dev.rocky.data.updates

import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

internal fun expectedChecksum(text: String, filename: String): String {
    val entries = text.lineSequence().mapNotNull {
        Regex("^([a-fA-F0-9]{64}) [ *](.+)$").matchEntire(it.trim())
    }.filter { it.groupValues[2] == filename }.toList()
    require(entries.size == 1) { "Checksum ausente ou duplicado para o instalador." }
    return entries.single().groupValues[1].lowercase()
}

fun updateChecksum(path: Path): String {
    val digest = MessageDigest.getInstance("SHA-256")
    Files.newInputStream(path).use { input ->
        val buffer = ByteArray(64 * 1024)
        while (true) {
            if (Thread.currentThread().isInterrupted) throw InterruptedException()
            val count = input.read(buffer)
            if (count < 0) break
            digest.update(buffer, 0, count)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}
