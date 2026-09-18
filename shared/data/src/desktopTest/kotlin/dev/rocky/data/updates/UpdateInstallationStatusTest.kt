package dev.rocky.data.updates

import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.*

class UpdateInstallationStatusTest {
    private fun fixture(state: String, test: (Path, Path) -> Unit) {
        val directory = Files.createTempDirectory("rocky-status-test-")
        try {
            val job = Files.createTempDirectory(directory, "install-")
            Files.writeString(directory.resolve("latest-installation"), job.fileName.toString())
            Files.writeString(job.resolve("status"), state)
            test(directory, job)
        } finally { directory.toFile().deleteRecursively() }
    }

    @Test fun consumesSuccessNoticeAndKeepsLogs() = fixture("installed") { directory, job ->
        assertEquals("update-installed", installationNotice(directory))
        assertNull(installationNotice(directory))
