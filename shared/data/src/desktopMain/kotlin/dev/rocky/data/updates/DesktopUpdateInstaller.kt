package dev.rocky.data.updates

import dev.rocky.core.updates.*
import java.nio.file.*

class DesktopUpdateInstaller(private val directory: Path) : UpdateInstaller {
    private val downloader = UpdateDownloader(directory)
    private val environment = InstallationEnvironment()
    private val helper = UpdateHelperLauncher()
    override fun download(update: AvailableUpdate, onProgress: (Long, Long) -> Unit) = downloader.download(update, onProgress)
    override fun cancel() { downloader.cancel(); helper.cancel() }
    override fun installationUnavailableReason() = environment.unavailableReason()
    override fun installationNotice() = installationNotice(directory)

    @Synchronized
    override fun open(update: PreparedUpdate) {
        val path = Path.of(update.path).toRealPath()
        require(path.startsWith(directory.toRealPath()) && Files.isRegularFile(path))
        require(updateChecksum(path) == update.sha256) { "O instalador foi alterado. Baixe novamente." }
        val os = System.getProperty("os.name").lowercase()
        val command = when {
            os.startsWith("mac") && path.toString().endsWith(".dmg") -> listOf("/usr/bin/open", path.toString())
            os.startsWith("windows") && path.toString().endsWith(".msi") -> listOf(
                Path.of(System.getenv("SystemRoot") ?: "C:\\Windows", "System32", "msiexec.exe").toString(),
                "/i", path.toString(), "/norestart", "/log", path.resolveSibling("installation.log").toString(),
            )
            else -> error("Formato de instalador incompatível.")
        }
        ProcessBuilder(command).start()
    }
}
