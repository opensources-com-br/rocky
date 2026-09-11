package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun SupportContent() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
        Text(
            text = "Super Chats ainda não estão conectados.",
            modifier = Modifier.padding(top = 24.dp),
            color = RockyColors.TextPrimary,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = "A conexão atual da Twitch recebe somente mensagens do chat.",
            modifier = Modifier.padding(top = 6.dp),
            color = RockyColors.TextSecondary,
            style = MaterialTheme.typography.body2,
        )
    }
}
