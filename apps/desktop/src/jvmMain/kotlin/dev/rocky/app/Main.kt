package dev.rocky.app

import androidx.compose.runtime.LaunchedEffect
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
import dev.rocky.ui.window.RockyWindow
import java.awt.Dimension

fun main() = application {
    val windowState = rememberWindowState(size = ExpandedSize)
    var compact by remember { mutableStateOf(false) }
    var pinned by remember { mutableStateOf(false) }
    var previousSize by remember { mutableStateOf(ExpandedSize) }

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
            onClose = ::exitApplication,
            onMinimize = { windowState.isMinimized = true },
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
        )
    }
}

private val ExpandedSize = DpSize(420.dp, 720.dp)
private val CompactSize = DpSize(340.dp, 180.dp)
