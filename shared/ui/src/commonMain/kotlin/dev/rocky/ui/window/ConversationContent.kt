package dev.rocky.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun ConversationContent(
    messages: List<ChatMessage> = emptyList(),
    streamerSpeech: String? = null,
    textRequestEnabled: Boolean = false,
    showTextRequest: Boolean = textRequestEnabled,
    analyzing: Boolean = false,
    hasCaptureGaps: Boolean = false,
    analysisStatus: String? = null,
    performanceNotice: String? = null,
    onCancelAnalysis: () -> Unit = {},
    onTextRequest: (String) -> Unit = {},
) {
    val chatScrollState = rememberLazyListState()
    LaunchedEffect(messages.lastOrNull()?.id) {
        if (messages.isNotEmpty()) chatScrollState.scrollToItem(messages.lastIndex)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "CHAT AO VIVO",
                color = RockyColors.TextMuted,
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${messages.size} ${if (messages.size == 1) "mensagem" else "mensagens"}",
                color = RockyColors.TextMuted,
                style = MaterialTheme.typography.caption,
            )
        }

        Text(
            text = tr("AI uses up to 200 received messages from the last two minutes; this is a limited sample.",
                "A IA usa até 200 mensagens recebidas nos últimos dois minutos; o contexto é uma amostra limitada."),
            style = MaterialTheme.typography.caption,
            color = RockyColors.TextMuted,
        )
        if (hasCaptureGaps) {
            Text(tr("Connection interrupted: some chat messages may be missing.",
                "Houve interrupção de conexão: algumas mensagens podem estar ausentes."),
                style = MaterialTheme.typography.caption, color = RockyColors.Accent)
        }
        if (showTextRequest) {
            StreamerTextRequest(enabled = textRequestEnabled && messages.isNotEmpty(), onSend = onTextRequest)
            if (analyzing) {
                androidx.compose.material.TextButton(onClick = onCancelAnalysis) {
                    Text(tr("Cancel analysis", "Cancelar análise"))
                }
            }
        }

        analysisStatus?.let { Text(it, style = MaterialTheme.typography.caption, color = RockyColors.TextSecondary) }
        performanceNotice?.let { Text(it, style = MaterialTheme.typography.caption, color = RockyColors.TextMuted) }
        streamerSpeech?.let { speech ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "VOCÊ",
                    modifier = Modifier.width(60.dp),
                    color = RockyColors.Accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = speech,
                    modifier = Modifier.weight(1f),
                    color = RockyColors.TextPrimary,
                    style = MaterialTheme.typography.body1,
                )
            }
        }

        if (messages.isEmpty()) {
            Text(
                text = "Aguardando mensagens do chat…",
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.body1,
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).testTag("chat-messages"),
                state = chatScrollState,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(messages, key = { it.id }) { ChatMessageRow(it) }
            }
        }
    }
}


@Composable
private fun ChatMessageRow(message: ChatMessage) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(7.dp)
                .background(message.platform.color(), CircleShape),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message.author,
                    color = RockyColors.TextPrimary,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = message.platform.label,
                    color = RockyColors.TextMuted,
                    fontSize = 10.sp,
                )
            }
            Text(
                text = message.text,
                modifier = Modifier.padding(top = 2.dp),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.body1,
            )
        }
    }
}

private val StreamPlatform.label: String
    get() = when (this) {
        StreamPlatform.Twitch -> "Twitch"
        StreamPlatform.Kick -> "Kick"
        StreamPlatform.YouTube -> "YouTube"
    }

private fun StreamPlatform.color(): Color = when (this) {
    StreamPlatform.Twitch -> RockyColors.Twitch
    StreamPlatform.Kick -> RockyColors.Kick
    StreamPlatform.YouTube -> RockyColors.YouTube
}
