package dev.rocky.ui.window

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun WindowActions(
    compact: Boolean,
    pinned: Boolean,
    onMinimize: () -> Unit,
    onTogglePinned: () -> Unit,
    onToggleCompact: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(onClick = onMinimize) {
            Text("Minimize")
        }
        OutlinedButton(
            modifier = Modifier.wrapContentWidth(),
            onClick = onTogglePinned,
        ) {
            Text(if (pinned) "Pinned" else "Pin")
        }
        Button(onClick = onToggleCompact) {
            Text(if (compact) "Expand" else "Compact")
        }
    }
}
