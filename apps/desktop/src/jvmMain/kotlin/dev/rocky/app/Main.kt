package dev.rocky.app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.rocky.data.notes.SqliteNoteRepository
import dev.rocky.platform.desktop.RockyDesktopPaths
import dev.rocky.platform.desktop.exportNotesAsMarkdown
import dev.rocky.ui.window.RockyWindow
import java.awt.Dimension

fun main() = application {
    val windowState = rememberWindowState(size = ExpandedSize)
    var compact by remember { mutableStateOf(false) }
    var pinned by remember { mutableStateOf(false) }
    var previousSize by remember { mutableStateOf(ExpandedSize) }
    var mainSizeBeforeSettings by remember { mutableStateOf(ExpandedSize) }
    val noteRepository = remember { SqliteNoteRepository(RockyDesktopPaths.notesDatabase) }

    DisposableEffect(noteRepository) {
        onDispose(noteRepository::close)
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Rocky",
        resizable = true,
        alwaysOnTop = pinned,
    ) {
        LaunchedEffect(window) {
            window.minimumSize = Dimension(340, 180)
        }
        RockyWindow(
            compact = compact,
            pinned = pinned,
            noteRepository = noteRepository,
            onExportNotes = { notes -> exportNotesAsMarkdown(window, notes) },
            onTogglePinned = { pinned = !pinned },
            onToggleCompact = {
                if (compact) {
                    windowState.size = previousSize
                } else {
                    previousSize = windowState.size
                    windowState.placement = WindowPlacement.Floating
                    windowState.size = CompactSize
                }
                compact = !compact
            },
            onSettingsVisibilityChanged = { open ->
                if (!compact) {
                    if (open) {
                        mainSizeBeforeSettings = windowState.size
                        windowState.size = SettingsSize
                    } else {
                        windowState.size = mainSizeBeforeSettings
                    }
                }
            },
        )
    }
}

private val ExpandedSize = DpSize(420.dp, 720.dp)
private val SettingsSize = DpSize(420.dp, 520.dp)
private val CompactSize = DpSize(340.dp, 180.dp)
