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

    fun launch(directory: Path, app: InstalledApplication, update: PreparedUpdate) {
        val job = Files.createTempDirectory(directory, "install-")
        activeJob = job
        Files.writeString(directory.resolve("latest-installation"), job.fileName.toString())
        Files.writeString(job.resolve("status"), "preparing")
        val name = if (app.mac) "install-macos.sh" else "install-windows.ps1"
        val script = job.resolve(name)
        javaClass.getResourceAsStream("/updates/$name").use { input ->
            checkNotNull(input) { "O atualizador não foi incluído nesta instalação." }
            Files.copy(input, script)
        }
        val nativeVersion = update.version.removePrefix("v").substringBefore('-')
        val arguments = listOf(update.path, app.target.toString(), ProcessHandle.current().pid().toString(),
            job.toString(), update.sha256, nativeVersion, update.version.removePrefix("v"))
        val command = if (app.mac) listOf("/usr/bin/nohup", "/bin/bash", script.toString()) + arguments
        else {
            val system = Path.of(System.getenv("SystemRoot") ?: "C:\\Windows", "System32")
            listOf(system.resolve("WindowsPowerShell/v1.0/powershell.exe").toString(),
                "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-WindowStyle", "Hidden",
                "-File", script.toString()) + arguments
