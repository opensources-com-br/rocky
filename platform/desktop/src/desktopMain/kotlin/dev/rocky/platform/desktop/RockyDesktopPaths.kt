package dev.rocky.platform.desktop

import java.nio.file.Path

object RockyDesktopPaths {
    val notesDatabase: Path
        get() = dataDirectory.resolve("rocky.db")

    val voiceDirectory: Path
        get() = dataDirectory.resolve("voice")

    private val dataDirectory: Path
        get() {
            val home = Path.of(System.getProperty("user.home"))
            val operatingSystem = System.getProperty("os.name").lowercase()
            return when {
                operatingSystem.contains("win") -> {
                    val appData = System.getenv("APPDATA")?.let(Path::of)
                        ?: home.resolve("AppData").resolve("Roaming")
                    appData.resolve("Rocky")
                }
                operatingSystem.contains("mac") -> home
                    .resolve("Library")
                    .resolve("Application Support")
                    .resolve("Rocky")
                else -> {
                    val xdgData = System.getenv("XDG_DATA_HOME")?.let(Path::of)
                        ?: home.resolve(".local").resolve("share")
                    xdgData.resolve("rocky")
                }
            }
        }
}
