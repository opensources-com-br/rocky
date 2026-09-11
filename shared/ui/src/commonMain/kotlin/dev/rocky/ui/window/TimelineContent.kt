package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rocky.core.live.LiveIdea
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun TimelineContent(
    onExportIdeas: (List<LiveIdea>) -> Boolean = { false },
) {
    var exportNotice by remember { mutableStateOf<String?>(null) }
    val visibleIdeas = emptyList<LiveIdea>()
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Ideias da live", style = MaterialTheme.typography.subtitle1)
                exportNotice?.let {
                    Text(text = it, color = RockyColors.Accent, style = MaterialTheme.typography.caption)
                }
            }
            OutlinedButton(
                onClick = { if (onExportIdeas(visibleIdeas)) exportNotice = "Markdown exportado." },
                enabled = false,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.TextPrimary),
            ) {
                Icon(Icons.Outlined.Download, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Exportar .md")
            }
        }
        Text(
            text = "A geração automática de ideias ainda não está disponível em sessões reais.",
            modifier = Modifier.padding(vertical = 28.dp),
            color = RockyColors.TextSecondary,
            style = MaterialTheme.typography.body2,
        )
    }
}
