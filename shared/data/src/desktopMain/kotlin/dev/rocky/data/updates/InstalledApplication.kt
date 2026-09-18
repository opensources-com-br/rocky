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
