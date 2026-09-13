package dev.rocky.data.updates

import dev.rocky.core.updates.*
import java.nio.file.*

class UpdateDownloader(private val directory: Path) {
    @Volatile private var transfer: UpdateTransfer? = null

    fun cancel() { transfer?.cancel() }

    @Synchronized
    fun download(update: AvailableUpdate, onProgress: (Long, Long) -> Unit): PreparedUpdate {
        require(ReleaseVersion.parse(update.version) != null)
        val asset = installerAsset(update, System.getProperty("os.name"), System.getProperty("os.arch"))
        require(asset.size in 1..2_147_483_648L) { "Tamanho de instalador inválido." }
        require(asset.url == "$RELEASE_DOWNLOAD${update.version}/${asset.name}")
        val sums = update.assets.singleOrNull { it.name == "SHA256SUMS.txt" }
            ?: error("A versão não publicou checksums. Use a página de releases.")
        require(sums.url == "$RELEASE_DOWNLOAD${update.version}/SHA256SUMS.txt")
        val connection = UpdateTransfer().also { transfer = it }
        Files.createDirectories(directory)
        val staging = Files.createTempDirectory(directory, "download-")
        val partial = staging.resolve(asset.name + ".part")
        var complete = false
        try {
            val checksum = connection.read(sums.url) { input ->
                val bytes = input.readNBytes(256 * 1024 + 1)
                require(bytes.size <= 256 * 1024) { "Arquivo de checksums excede o limite." }
                expectedChecksum(bytes.toString(Charsets.UTF_8), asset.name)
            }
            connection.read(asset.url) { input ->
                Files.newOutputStream(partial, StandardOpenOption.CREATE_NEW).use { output ->
                    val buffer = ByteArray(64 * 1024)
                    var total = 0L
                    onProgress(0, asset.size)
                    while (true) {
                        connection.checkCancelled()
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        require(total <= asset.size) { "Instalador maior que o anunciado." }
                        output.write(buffer, 0, count)
                        onProgress(total, asset.size)
                    }
                    require(total == asset.size) { "Download incompleto. Tente novamente." }
                }
            }
            require(updateChecksum(partial) == checksum) { "O instalador não passou na verificação de integridade." }
            connection.checkCancelled()
            val target = staging.resolve(asset.name)
            Files.move(partial, target, StandardCopyOption.ATOMIC_MOVE)
            complete = true
            return PreparedUpdate(update.version, target.toString(), checksum)
        } finally {
            connection.cancel()
            transfer = null
            if (!complete) { Files.deleteIfExists(partial); Files.deleteIfExists(staging) }
        }
    }
}
