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
        assertTrue(Files.exists(job.resolve("status")))
    }

    @Test fun reportsFailureAndWindowsRestart() {
        fixture("failed") { directory, _ -> assertEquals("update-failed", installationNotice(directory)) }
        fixture("restart-required") { directory, _ -> assertEquals("restart-required", installationNotice(directory)) }
    }

    @Test fun detectsAbandonedHelper() = fixture("installing") { directory, _ ->
        assertEquals("update-interrupted", installationNotice(directory))
    }

    @Test fun leavesRunningHelperAlone() = fixture("ready") { directory, job ->
        Files.writeString(job.resolve("helper.pid"), ProcessHandle.current().pid().toString())
        assertNull(installationNotice(directory))
        assertTrue(Files.exists(directory.resolve("latest-installation")))
    }

    @Test fun ignoresUnsafeStatusPointer() = fixture("failed") { directory, _ ->
        Files.writeString(directory.resolve("latest-installation"), "../installation")
        assertNull(installationNotice(directory))
    }

    @Test fun doesNotShowCancelledAttemptAsFailure() = fixture("cancelled") { directory, _ ->
        assertNull(installationNotice(directory))
    }
}
