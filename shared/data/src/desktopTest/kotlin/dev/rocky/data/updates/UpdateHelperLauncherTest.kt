package dev.rocky.data.updates

import dev.rocky.core.updates.PreparedUpdate
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.*

class UpdateHelperLauncherTest {
    private class RunningProcess(private val running: Boolean = true) : Process() {
        override fun getOutputStream() = ByteArrayOutputStream()
        override fun getInputStream() = "".byteInputStream()
        override fun getErrorStream() = "".byteInputStream()
        override fun waitFor() = 0
        override fun exitValue() = 0
        override fun destroy() = Unit
        override fun isAlive() = running
        override fun pid() = 123456789L
    }

    private fun fixture(test: (Path, InstalledApplication, PreparedUpdate) -> Unit) {
        val directory = Files.createTempDirectory("rocky helper test ")
        try {
            val app = InstalledApplication(Path.of("/Applications/Rocky.app/Contents/MacOS/Rocky"),
                Path.of("/Applications/Rocky.app"), true)
            test(directory, app, PreparedUpdate("v2.0.0-alpha.1", directory.resolve("package with spaces.dmg").toString(), "a".repeat(64)))
        } finally { directory.toFile().deleteRecursively() }
    }

    @Test fun launchesBundledHelperAndWaitsForReadiness() = fixture { directory, app, update ->
        var launchedJob: Path? = null
        val launcher = UpdateHelperLauncher({ command, job ->
            assertEquals(listOf("/usr/bin/nohup", "/bin/bash"), command.take(2))
            assertEquals(update.path, command[3])
            assertEquals("2.0.0", command[8])
            assertEquals("2.0.0-alpha.1", command[9])
            assertTrue(Files.readString(Path.of(command[2])).startsWith("#!/bin/bash"))
            launchedJob = job
            Files.writeString(job.resolve("ready"), "ready")
            RunningProcess()
        }, timeoutMillis = 500)
        launcher.launch(directory, app, update)
        val preparedJob = assertNotNull(launchedJob)
        assertFalse(Files.exists(preparedJob.resolve("cancel")))
        launcher.cancel()
        assertTrue(Files.exists(preparedJob.resolve("cancel")))
    }

    @Test fun cancelsHelperWhenPreparationTimesOut() = fixture { directory, app, update ->
        var job: Path? = null
        val launcher = UpdateHelperLauncher({ _, path -> job = path; RunningProcess() }, timeoutMillis = 1)
        assertFailsWith<IllegalStateException> { launcher.launch(directory, app, update) }
        assertTrue(Files.exists(job!!.resolve("cancel")))
    }

    @Test fun neverRequestsRestartWhenHelperFails() = fixture { directory, app, update ->
        val launcher = UpdateHelperLauncher({ _, _ -> RunningProcess(false) })
        assertFailsWith<IllegalStateException> { launcher.launch(directory, app, update) }
    }

    @Test fun cancellationTakesPrecedenceOverReady() = fixture { directory, app, update ->
        val launcher = UpdateHelperLauncher({ _, job ->
            Files.writeString(job.resolve("ready"), "ready")
            Files.writeString(job.resolve("cancel"), "cancel")
            RunningProcess()
        })
        assertFailsWith<IllegalStateException> { launcher.launch(directory, app, update) }
    }

    @Test fun windowsUsesPackagedPowerShellWithoutShellInterpolation() = fixture { directory, app, update ->
        val launcher = UpdateHelperLauncher({ command, job ->
            assertTrue(command.first().endsWith("powershell.exe"))
            assertEquals(update.path, command[9])
            assertTrue(Files.readString(Path.of(command[8])).contains("[string]\$Package"))
            Files.writeString(job.resolve("ready"), "ready")
            RunningProcess()
        })
        launcher.launch(directory, app.copy(mac = false), update)
    }
}
