package dev.rocky.data.updates

import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.*

class InstalledApplicationTest {
    private fun fixture(mac: Boolean, directory: String = "Applications", test: (InstallationEnvironment) -> Unit) {
        val home = Files.createTempDirectory("rocky-installation-test-")
        try {
            val root = home.resolve(directory).resolve(if (mac) "Rocky.app" else "Rocky")
            val executable = root.resolve(if (mac) "Contents/MacOS/Rocky" else "Rocky.exe")
            val config = root.resolve(if (mac) "Contents/app/Rocky.cfg" else "app/Rocky.cfg")
            val runtime = root.resolve(if (mac) "Contents/runtime/Contents/Home" else "runtime")
            Files.createDirectories(executable.parent)
            Files.createDirectories(config.parent)
            Files.createDirectories(runtime)
            Files.writeString(executable, "launcher")
            Files.writeString(config, "[JavaOptions]")
            test(InstallationEnvironment(if (mac) "Mac OS X" else "Windows 11", executable.toString(), runtime, home))
        } finally { home.toFile().deleteRecursively() }
    }

    @Test fun acceptsInstalledMacBundle() = fixture(true) {
        assertNull(it.unavailableReason())
        assertTrue(it.application()!!.mac)
    }

    @Test fun refusesMacDiskImageOrUninstalledCopy() = fixture(true, "Downloads") {
        assertEquals("not-installed", it.unavailableReason())
    }

    @Test fun detectsWindowsPackagedApplication() = fixture(false) {
        assertNull(it.unavailableReason())
        assertFalse(it.application()!!.mac)
    }

    @Test fun refusesDevelopmentLauncherAndUnsupportedPlatform() {
        assertEquals("development-build", InstallationEnvironment("Mac OS X", null).unavailableReason())
        assertEquals("unsupported-platform", InstallationEnvironment("Linux", null).unavailableReason())
    }

    @Test fun refusesExternalJavaRuntime() = fixture(true) {
        val app = it.application()!!
        val external = InstallationEnvironment("Mac OS X", app.executable.toString(), Path.of(System.getProperty("java.home")))
        assertEquals("development-build", external.unavailableReason())
    }
}
