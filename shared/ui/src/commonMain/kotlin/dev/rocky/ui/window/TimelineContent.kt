package dev.rocky.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.core.live.LiveIdea
import dev.rocky.core.live.LiveNote
import dev.rocky.ui.theme.RockyColors

private data class TimelineEntry(val time: String, val text: String, val tag: String)

private val notes = listOf(
    TimelineEntry("01:38", "Prometeu mostrar o raio-x do repositório antes de encerrar.", "PENDÊNCIA"),
    TimelineEntry("01:24", "Chat reagiu forte à parte de deploy — bom material para um corte.", "CLIPE"),
    TimelineEntry("01:11", "Três pedidos de compatibilidade com Next.js. Ninguém respondeu ainda.", "DÚVIDA"),
)

private val ideas = listOf(
    LiveIdea("Série curta respondendo as 5 dúvidas mais repetidas do chat.", "01:31", "CONTEÚDO"),
    LiveIdea("Enquete ao vivo: deploy manual ou CI? O chat está dividido.", "01:05", "INTERAÇÃO"),
    LiveIdea("Convidar a Ju para a próxima live — ela respondeu metade do chat.", "00:48", "CONVITE"),
)

@Composable
internal fun TimelineContent(
    section: MainSection,
    savedNotes: List<LiveNote> = emptyList(),
    demonstration: Boolean = true,
    onExportIdeas: (List<LiveIdea>) -> Boolean = { false },
) {
    var exportNotice by remember { mutableStateOf<String?>(null) }
    val visibleIdeas = if (demonstration) ideas else emptyList()
    val entries = if (section == MainSection.Notes) {
        savedNotes.map { note ->
            TimelineEntry(note.timestamp, note.text, note.tag)
        } + notes
    } else {
        visibleIdeas.map { idea -> TimelineEntry(idea.timestamp, idea.text, idea.tag) }
    }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
        if (section == MainSection.Ideas) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ideias da live", style = MaterialTheme.typography.subtitle1)
                    exportNotice?.let {
                        Text(
                            text = it,
                            color = RockyColors.Accent,
                            style = MaterialTheme.typography.caption,
                        )
                    }
                }
                OutlinedButton(
                    onClick = {
                        if (onExportIdeas(visibleIdeas)) exportNotice = "Markdown exportado."
                    },
                    enabled = visibleIdeas.isNotEmpty(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.TextPrimary),
                ) {
                    Icon(Icons.Outlined.Download, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Exportar .md")
                }
            }
            if (visibleIdeas.isEmpty()) {
                Text(
                    text = "A geração automática de ideias ainda não está disponível em sessões reais.",
                    modifier = Modifier.padding(vertical = 28.dp),
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
        entries.forEachIndexed { index, entry ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                Text(
                    text = entry.time,
                    modifier = Modifier.width(48.dp).padding(top = 2.dp),
                    color = RockyColors.TextMuted,
                    style = MaterialTheme.typography.caption,
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.text,
                        color = RockyColors.TextPrimary,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = entry.tag,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .background(RockyColors.SurfaceElevated, RoundedCornerShape(12.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp),
                        color = RockyColors.TextSecondary,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                    )
                }
            }
            if (index < entries.lastIndex) {
                Divider(color = RockyColors.Divider)
            }
        }
    }
}
