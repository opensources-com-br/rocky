package dev.rocky.platform.desktop

import java.awt.Desktop
import java.nio.file.Files
import java.util.prefs.Preferences

fun openRockyDataDirectory() {
    val path = RockyDesktopPaths.notesDatabase.parent
    Files.createDirectories(path)
    Desktop.getDesktop().open(path.toFile())
}

fun resetRockySettings() {
    AiDesktopPreferences.clear()
    KickDesktopPreferences.clear()
    YouTubeDesktopPreferences.clear()
    for (name in listOf("agent", "voice", "twitch", "kick", "youtube", "interface", "onboarding", "window", "shortcuts", "experience")) {
        Preferences.userRoot().node("dev/rocky/$name").apply { clear(); flush() }
    }
}

fun removeManagedVoiceModel() {
    val path = RockyDesktopPaths.voiceDirectory
    Files.deleteIfExists(path.resolve("ggml-base.bin.part"))
    Files.deleteIfExists(path.resolve("ggml-base.bin"))
}
