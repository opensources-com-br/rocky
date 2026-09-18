package dev.rocky.data.updates

import dev.rocky.core.updates.PreparedUpdate
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

internal class UpdateHelperLauncher(
    private val start: (List<String>, Path) -> Process = { command, job ->
        ProcessBuilder(command).directory(job.toFile()).redirectErrorStream(true)
            .redirectOutput(job.resolve("installation.log").toFile()).start()
    },
    private val timeoutMillis: Long = TimeUnit.MINUTES.toMillis(5),
) {
    @Volatile private var activeJob: Path? = null

    fun cancel() {
        activeJob?.let { runCatching { Files.writeString(it.resolve("cancel"), "cancel") } }
    }

