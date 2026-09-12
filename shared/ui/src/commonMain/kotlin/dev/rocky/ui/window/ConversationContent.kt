package dev.rocky.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    onQueue: () -> Unit = {},
    pendingQuestions: Int = 0,
    filteredCount: Int = 0,
    onHistory: () -> Unit = {},
    onCancelAnalysis: () -> Unit = {},
    onTextRequest: (String) -> Unit = {},
) {
    var showDetails by remember { mutableStateOf(false) }
    if (showDetails) {
        androidx.compose.material.AlertDialog(
            onDismissRequest = { showDetails = false },
            title = { Text(tr("Analysis details", "Detalhes da análise")) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(tr("AI uses up to 200 received messages from the last two minutes; this is a limited sample.",
                    "A IA usa até 200 mensagens recebidas nos últimos dois minutos; o contexto é uma amostra limitada."))
                if (hasCaptureGaps) Text(tr("Some messages may be missing after a connection interruption.",
                    "Algumas mensagens podem estar ausentes após uma interrupção de conexão."))
                Text("$filteredCount " + tr("messages excluded from the last AI request by filters.", "mensagens excluídas da última análise pelos filtros."))
                analysisStatus?.let { Text(it) }
                performanceNotice?.let { Text(it) }
                Text(tr("Reported tokens are partial and exclude unreported usage, tests and some retries. Cancellation does not reverse charges.",
                    "Tokens são parciais e excluem uso não informado, testes e algumas tentativas. Cancelar não reverte cobranças."))
            } },
            confirmButton = { androidx.compose.material.TextButton(onClick = { showDetails = false }) { Text("OK") } },
        )
    }
    val chatScrollState = rememberLazyListState()
    LaunchedEffect(messages.lastOrNull()?.id) {
        if (messages.isNotEmpty()) chatScrollState.scrollToItem(messages.lastIndex)
    }

    BoxWithConstraints(Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 6.dp)) {
    val controlsMaxHeight = (maxHeight - 83.dp).coerceAtLeast(0.dp)
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        // Keep chat visible even when fonts, status messages or controls need more room.
        Column(
            Modifier.heightIn(max = controlsMaxHeight)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.material.TextButton(onClick = onHistory) { Text(tr("History", "Histórico")) }
            androidx.compose.material.TextButton(onClick = onQueue) {
                Text(tr("Questions", "Perguntas") + " ($pendingQuestions)", style = MaterialTheme.typography.caption)
            }
            Spacer(Modifier.weight(1f))
            androidx.compose.material.TextButton(onClick = { showDetails = true }) {
                Text(if (hasCaptureGaps) tr("Chat incomplete", "Chat incompleto") else tr("Sample · details", "Amostra · detalhes"),
                    color = if (hasCaptureGaps) RockyColors.Accent else RockyColors.TextMuted,
                    style = MaterialTheme.typography.caption)
            }
            Text(
                text = "${messages.size} ${if (messages.size == 1) "mensagem" else "mensagens"}",
                color = RockyColors.TextMuted,
                style = MaterialTheme.typography.caption,
            )
        }

        if (showTextRequest) {
            StreamerTextRequest(enabled = textRequestEnabled, onSend = onTextRequest)
        }
        QuickQuestions(textRequestEnabled, onTextRequest)
        if (analyzing) {
            androidx.compose.material.TextButton(onClick = onCancelAnalysis) {
                Text(tr("Cancel analysis", "Cancelar análise"))
            }
        }

        if (analysisStatus?.startsWith("Não foi possível") == true) {
            Text(analysisStatus, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption, color = RockyColors.Accent)
        }
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
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = RockyColors.TextPrimary,
                    style = MaterialTheme.typography.body1,
                )
            }
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
                verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.Bottom),
            ) {
                items(messages, key = { it.id }) { ChatMessageRow(it) }
            }
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
