package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rocky.core.live.*

@Composable
internal fun QuestionQueueDialog(records: LocalNotesState, sessionId: String, onDismiss: () -> Unit) {
    var answered by remember { mutableStateOf(false) }
    val questions = records.notes.filter { it.tag == QUESTION_TAG && it.sessionId == sessionId }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(tr("Grouped questions", "Perguntas agrupadas")) },
        text = { Column {
            Text(tr("Similar wording; review the sources. Up to 100 groups per live.", "Texto semelhante; confira as fontes. Até 100 grupos por live."))
            Row {
                TextButton(onClick = { answered = false }) { Text(tr("Pending", "Pendentes") + " (${questions.count { !it.completed }})") }
                TextButton(onClick = { answered = true }) { Text(tr("Answered", "Respondidas") + " (${questions.count { it.completed }})") }
            }
            LazyColumn(Modifier.heightIn(max = 400.dp)) {
                items(questions.filter { it.completed == answered }, key = { it.id }) { question ->
                    Text(question.text)
                    Text("${question.messageCount} " + tr("grouped messages", "mensagens agrupadas"), style = MaterialTheme.typography.caption)
                    EvidenceButton(question.evidence)
                    TextButton(onClick = { records.update(question.copy(completed = !question.completed)) }) {
                        Text(if (question.completed) tr("Reopen", "Reabrir") else tr("Mark answered", "Marcar respondida"))
                    }
                    Divider()
                }
            }
        } },
        confirmButton = { TextButton(onClick = onDismiss) { Text(tr("Close", "Fechar")) } })
}
