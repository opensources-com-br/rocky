package dev.rocky.data.updates

import dev.rocky.core.updates.*
import java.io.InputStream
import java.nio.file.Files
import kotlin.test.*

class UpdateDownloaderTest {
    private val hash = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
    private fun runDownload(body: String, checksum: String = hash, cancel: Boolean = false) {
        val directory = Files.createTempDirectory("rocky-download-test-")
        val names = listOf("darwin-arm64.dmg", "darwin-x86_64.dmg", "windows-amd64.msi", "windows-arm64.msi")
        val assets = names.map { name -> "Rocky-2.0.0-$name" }.map { ReleaseAsset(it, "${RELEASE_DOWNLOAD}v2.0.0/$it", 3) }
        val update = AvailableUpdate("v2.0.0", "", assets + ReleaseAsset("SHA256SUMS.txt", "${RELEASE_DOWNLOAD}v2.0.0/SHA256SUMS.txt", 100))
        val source = object : UpdateTransfer() {
            override fun <T> read(url: String, consume: (InputStream) -> T): T {
                val text = if (url.endsWith("SHA256SUMS.txt")) assets.joinToString("\n") { "$checksum  ${it.name}" } else body
                return text.byteInputStream().use(consume)
            }
        }
        val downloader = UpdateDownloader(directory) { source }
        try {
            if (body == "abc" && checksum == hash && !cancel) {
                val ready = downloader.download(update) { _, _ -> }
                assertEquals("abc", Files.readString(java.nio.file.Path.of(ready.path)))
                assertEquals(hash, ready.sha256)
            } else {
                assertFails { downloader.download(update) { _, _ -> if (cancel) downloader.cancel() } }
                Files.list(directory).use { assertEquals(0L, it.count()) }
            }
        } finally { directory.toFile().deleteRecursively() }
    }
}
