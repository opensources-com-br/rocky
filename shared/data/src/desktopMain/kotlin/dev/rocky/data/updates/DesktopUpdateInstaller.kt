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
    }
}

internal fun validatePreparedUpdate(directory: Path, update: PreparedUpdate, current: String?, os: String, arch: String): Path {
    val version = requireNotNull(ReleaseVersion.parse(update.version)) { "Versão de atualização inválida." }
    val installed = requireNotNull(current?.let(ReleaseVersion::parse)) { "Build sem versão de release." }
    require(version > installed) { "Esta atualização já está instalada ou é mais antiga." }
    val source = Path.of(update.path)
    val path = source.toRealPath()
    require(!Files.isSymbolicLink(source) && Files.isRegularFile(path)) { "Caminho de instalador inválido." }
    require(path.parent.parent == directory.toRealPath() && path.parent.fileName.toString().startsWith("download-")) {
        "O instalador não pertence ao download verificado."
    }
    installerAsset(AvailableUpdate(update.version, "", listOf(ReleaseAsset(path.fileName.toString(), "", Files.size(path)))), os, arch)
    require(Regex("[a-f0-9]{64}").matches(update.sha256) && updateChecksum(path) == update.sha256) {
        "O instalador foi alterado. Baixe novamente."
    }
    return path
}
