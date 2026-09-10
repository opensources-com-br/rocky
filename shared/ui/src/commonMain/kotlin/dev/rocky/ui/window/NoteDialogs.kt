package dev.rocky.ui.window

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.rocky.core.live.LiveNote
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun EditNoteDialog(
    note: LiveNote,
    onDismiss: () -> Unit,
    onSave: (LiveNote) -> Unit,
) {
    var text by remember(note.id) { mutableStateOf(note.text) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar nota") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Conteúdo") },
                minLines = 4,
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(note.copy(text = text.trim())) },
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = RockyColors.Accent,
                    contentColor = Color.Black,
                ),
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        backgroundColor = RockyColors.Surface,
        contentColor = RockyColors.TextPrimary,
    )
}

@Composable
internal fun DeleteNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir nota?") },
        text = { Text("Esta ação remove a nota deste computador.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Excluir", color = RockyColors.YouTube)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        backgroundColor = RockyColors.Surface,
        contentColor = RockyColors.TextPrimary,
    )
}
