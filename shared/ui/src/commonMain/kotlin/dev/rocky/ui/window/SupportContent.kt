package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

private data class SupportMessage(
    val author: String,
    val amount: String,
    val message: String,
    val platformColor: Color,
    val read: Boolean = false,
)

private val supportMessages = listOf(
    SupportMessage("ju.lia", "R$ 50", "Essa aula salvou minha semana, obrigada!", RockyColors.YouTube),
    SupportMessage("marcos_dev", "R$ 25", "Vale a pena usar isso em produção hoje?", RockyColors.Twitch),
    SupportMessage("day", "R$ 10", "Quantas vagas ainda tem na turma?", RockyColors.Kick, read = true),
)

@Composable
internal fun SupportContent() {
    var readAuthors by remember { mutableStateOf(emptySet<String>()) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
        supportMessages.forEach { item ->
            SupportCard(
                item = item.copy(read = item.read || item.author in readAuthors),
                onRead = { readAuthors = readAuthors + item.author },
            )
        }
    }
}

@Composable
private fun SupportCard(item: SupportMessage, onRead: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        color = if (item.read) RockyColors.SurfaceElevated else Color(0xFF2A1E1A),
        shape = RoundedCornerShape(13.dp),
        border = BorderStroke(1.dp, if (item.read) RockyColors.Border else RockyColors.AccentMuted),
    ) {
        Column(modifier = Modifier.padding(13.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.size(8.dp).background(item.platformColor, CircleShape))
                Text(
                    text = item.author,
                    modifier = Modifier.padding(start = 8.dp),
                    color = RockyColors.TextPrimary,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = item.amount,
                    modifier = Modifier.padding(start = 9.dp),
                    color = if (item.read) RockyColors.TextMuted else RockyColors.Accent,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = if (item.read) "lido" else "ler agora",
                    color = if (item.read) RockyColors.TextMuted else RockyColors.Accent,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.clickable(enabled = !item.read, onClick = onRead),
                )
            }
            Text(
                text = item.message,
                modifier = Modifier.padding(top = 8.dp),
                color = if (item.read) RockyColors.TextSecondary else RockyColors.TextPrimary,
                style = MaterialTheme.typography.body1,
            )
        }
    }
}
