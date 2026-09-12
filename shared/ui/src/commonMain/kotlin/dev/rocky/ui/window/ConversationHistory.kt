package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

@Composable
internal fun ConversationHistory(
    entries: List<ConversationEntry>,
    onDismiss: () -> Unit,
    onRepeat: (String) -> Unit,
    onSave: (ConversationEntry, VoiceSaveTarget) -> Unit,
) {
    val clipboard = LocalClipboardManager.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("Conversation history", "Histórico da conversa")) },
        text = {
            LazyColumn(Modifier.heightIn(max = 450.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { Text(tr("Last 30 answers in this session. Cleared when disconnected.", "Últimas 30 respostas desta sessão. Limpo ao desconectar.")) }
                items(entries.asReversed(), key = { it.answer.id }) { entry ->
                    Column {
                        Text(entry.question, style = MaterialTheme.typography.subtitle2)
                        Text(entry.answer.text)
                        Row {
                            TextButton(onClick = { clipboard.setText(AnnotatedString(entry.answer.text)) }) { Text(tr("Copy", "Copiar")) }
                            TextButton(onClick = { onDismiss(); onRepeat(entry.question) }) { Text(tr("Repeat", "Repetir")) }
                        }
                        Row {
                            TextButton(onClick = { onSave(entry, VoiceSaveTarget.Note) }) { Text(tr("Save note", "Salvar nota")) }
                            TextButton(onClick = { onSave(entry, VoiceSaveTarget.Idea) }) { Text(tr("Save idea", "Salvar ideia")) }
                        }
                        Divider()
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(tr("Close", "Fechar")) } },
    )
}
