package dev.rocky.ui.window

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun ConversationContent() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp),
    ) {
        DialogueLine("VOCÊ", "Rocky, o que o chat está achando da parte de deploy?")
        DialogueLine(
            "ROCKY",
            "Aprovado. Doze mensagens elogiaram, duas pediram para repetir o comando final.",
            assistant = true,
        )
        DialogueLine("VOCÊ", "Tem alguma dúvida repetida que eu deixei passar?")
        DialogueLine(
            "ROCKY",
            "Sim: compatibilidade com Next.js, três vezes nos últimos oito minutos.",
            assistant = true,
        )
    }
}

@Composable
private fun DialogueLine(
    speaker: String,
    message: String,
    assistant: Boolean = false,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = speaker,
            modifier = Modifier.width(58.dp).padding(top = 2.dp),
            color = if (assistant) RockyColors.Accent else RockyColors.TextMuted,
            fontSize = 11.sp,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            modifier = Modifier.weight(1f),
            color = RockyColors.TextPrimary,
            style = MaterialTheme.typography.body1,
        )
    }
}
