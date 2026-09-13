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

