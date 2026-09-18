package dev.rocky.data.updates

import java.nio.file.Files
import java.nio.file.Path

internal fun installationNotice(directory: Path): String? = runCatching {
    val pointer = directory.resolve("latest-installation")
    if (!Files.isRegularFile(pointer)) return null
    val name = Files.readString(pointer).trim()
    if (!Regex("install-[A-Za-z0-9-]+").matches(name)) return null
    val job = directory.resolve(name)
    val state = job.resolve("status")
    if (!Files.isRegularFile(state)) return null
    val notice = when (Files.readString(state).trim()) {
        "installed" -> "update-installed"
        "restart-required" -> "restart-required"
        "failed" -> "update-failed"
        "cancelled" -> return null
        else -> {
            val pid = runCatching { Files.readString(job.resolve("helper.pid")).trim().toLong() }.getOrNull()
            if (pid != null && ProcessHandle.of(pid).map { it.isAlive }.orElse(false)) return null
            "update-interrupted"
        }
    }
    Files.deleteIfExists(pointer)
    notice
}.getOrNull()
