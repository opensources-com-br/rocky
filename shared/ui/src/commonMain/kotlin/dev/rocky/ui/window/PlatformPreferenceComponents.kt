package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun PlatformPreferenceGroup(
    name: String,
    status: String,
    statusColor: Color,
    statusTag: String,
    onCreateApp: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    SettingsPreferenceGroup(name) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(tr("Connection", "Conexão"), style = MaterialTheme.typography.body2)
                Text(status, Modifier.padding(top = 4.dp).testTag(statusTag),
                    color = statusColor, style = MaterialTheme.typography.caption)
            }
            onCreateApp?.let { action ->
                TextButton(onClick = action) { Text(tr("Create app", "Criar app")) }
            }
        }
        Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
        Column(Modifier.fillMaxWidth().padding(16.dp), content = content)
    }
}

@Composable
internal fun platformFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    backgroundColor = RockyColors.Surface,
    textColor = RockyColors.TextPrimary,
    unfocusedBorderColor = RockyColors.Border,
    focusedBorderColor = RockyColors.Accent,
)

internal val PlatformFieldShape = RoundedCornerShape(8.dp)
