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
        check(installationUnavailableReason() == null) { "Instale o Rocky na pasta Aplicativos antes de atualizar." }
        val app = checkNotNull(environment.application())
        val path = validatePreparedUpdate(directory, update, System.getProperty("rocky.version"),
            System.getProperty("os.name"), System.getProperty("os.arch"))
        helper.launch(directory.toRealPath(), app, update.copy(path = path.toString()))
            else -> error("Formato de instalador incompatível.")
        }
        ProcessBuilder(command).start()
    }
}
