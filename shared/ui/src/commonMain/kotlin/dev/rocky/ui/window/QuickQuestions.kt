package dev.rocky.ui.window

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun QuickQuestions(enabled: Boolean, onAsk: (String) -> Unit) {
    val questions = listOf(
        tr("Main questions", "Dúvidas principais") to tr("Summarize the main questions in the received sample.", "Resuma as principais dúvidas da amostra recebida."),
        tr("What did I miss?", "O que perdi?") to tr("What important topics appeared in the recent sample? Explain its coverage limits.", "Quais assuntos importantes apareceram na amostra recente? Explique os limites da cobertura."),
        tr("Chat ideas", "Ideias do chat") to tr("Suggest ideas grounded in the received chat messages.", "Sugira ideias fundamentadas nas mensagens recebidas do chat."),
    )
    Row(Modifier.horizontalScroll(rememberScrollState())) {
        questions.forEach { (label, request) ->
            TextButton(enabled = enabled, contentPadding = PaddingValues(horizontal = 5.dp), onClick = { onAsk(request) }) {
                Text(label, style = MaterialTheme.typography.caption)
            }
        }
    }
}
