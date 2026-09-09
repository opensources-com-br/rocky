package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors
import dev.rocky.ui.theme.RockyTheme

@Composable
fun RockyWindow(
    compact: Boolean,
    pinned: Boolean,
    onMinimize: () -> Unit,
    onTogglePinned: () -> Unit,
    onToggleCompact: () -> Unit,
) {
    RockyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = RockyColors.Background,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                RockyHeader()
                Spacer(Modifier.height(12.dp))
                WindowActions(
                    compact = compact,
                    pinned = pinned,
                    onMinimize = onMinimize,
                    onTogglePinned = onTogglePinned,
                    onToggleCompact = onToggleCompact,
                )
                if (!compact) {
                    Spacer(Modifier.height(20.dp))
                    Divider(color = RockyColors.SurfaceElevated)
                    Spacer(Modifier.height(20.dp))
                    Text("Window controls are ready", style = MaterialTheme.typography.h6)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Live chat and assistant activity will appear here.",
                        color = RockyColors.TextSecondary,
                    )
                }
            }
        }
    }
}
