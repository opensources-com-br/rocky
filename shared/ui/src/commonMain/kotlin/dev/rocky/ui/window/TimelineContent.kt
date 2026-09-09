package dev.rocky.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.ui.theme.RockyColors

private data class TimelineEntry(val time: String, val text: String, val tag: String)

private val notes = listOf(
    TimelineEntry("01:38", "Prometeu mostrar o raio-x do repositório antes de encerrar.", "PENDÊNCIA"),
    TimelineEntry("01:24", "Chat reagiu forte à parte de deploy — bom material para um corte.", "CLIPE"),
    TimelineEntry("01:11", "Três pedidos de compatibilidade com Next.js. Ninguém respondeu ainda.", "DÚVIDA"),
)

private val ideas = listOf(
    TimelineEntry("01:31", "Série curta respondendo as 5 dúvidas mais repetidas do chat.", "CONTEÚDO"),
    TimelineEntry("01:05", "Enquete ao vivo: deploy manual ou CI? O chat está dividido.", "INTERAÇÃO"),
    TimelineEntry("00:48", "Convidar a Ju para a próxima live — ela respondeu metade do chat.", "CONVITE"),
)

@Composable
internal fun TimelineContent(section: MainSection) {
    val entries = if (section == MainSection.Notes) notes else ideas
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
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
