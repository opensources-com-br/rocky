package dev.rocky.data.updates

import java.nio.file.Files
import java.nio.file.Path

internal data class InstalledApplication(val executable: Path, val target: Path, val mac: Boolean)

internal class InstallationEnvironment(
    private val os: String = System.getProperty("os.name"),
    private val launcher: String? = System.getProperty("jpackage.app-path"),
    private val runtime: Path = Path.of(System.getProperty("java.home")),
    private val userHome: Path = Path.of(System.getProperty("user.home")),
) {
    fun unavailableReason(): String? {
        if (!os.startsWith("Mac", true) && !os.startsWith("Windows", true)) return "unsupported-platform"
        val app = application() ?: return "development-build"
        if (app.mac) {
            val locations = listOf(Path.of("/Applications"), userHome.resolve("Applications"))
            if (locations.none { app.target.parent == runCatching { it.toRealPath() }.getOrNull() }) return "not-installed"
            if (!Files.isWritable(app.target.parent)) return "read-only-installation"
        }
        return null
    }

    fun application(): InstalledApplication? = runCatching {
        val executable = Path.of(launcher ?: return null).toRealPath()
        val mac = os.startsWith("Mac", true)
        val target = if (mac) executable.parent.parent.parent else executable.parent
        val config = if (mac) target.resolve("Contents/app/Rocky.cfg") else target.resolve("app/Rocky.cfg")
        val runtimeRoot = if (mac) target.resolve("Contents/runtime") else target.resolve("runtime")
        if (mac && executable != target.resolve("Contents/MacOS/Rocky")) return null
        if (!mac && (!os.startsWith("Windows", true) || executable.fileName.toString() != "Rocky.exe")) return null
        if (mac && target.fileName.toString() != "Rocky.app") return null
        if (!Files.isRegularFile(config) || !runtime.toRealPath().startsWith(runtimeRoot.toRealPath())) return null
        InstalledApplication(executable, target, mac)
    }.getOrNull()
}
