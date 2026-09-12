package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rocky.core.live.LiveNote
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun NotesContent(
    notes: List<LiveNote>,
    notice: String?,
    onUpdate: (LiveNote) -> Unit,
    onDelete: (String) -> Unit,
    onExport: () -> Unit,
    title: String = "Notas locais",
    ideas: Boolean = false,
    loadFailed: Boolean = false,
    onReload: () -> Unit = {},
) {
    var editingNote by remember { mutableStateOf<LiveNote?>(null) }
    var deletingNote by remember { mutableStateOf<LiveNote?>(null) }

    editingNote?.let { note ->
        EditNoteDialog(
            note = note,
            onDismiss = { editingNote = null },
            onSave = {
                onUpdate(it)
                editingNote = null
            },
        )
    }
    deletingNote?.let { note ->
        DeleteNoteDialog(
            onDismiss = { deletingNote = null },
            onConfirm = {
                onDelete(note.id)
                deletingNote = null
            },
        )
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.subtitle1)
                Text(
                    text = "${notes.size} ${if (ideas) { if (notes.size == 1) "ideia salva" else "ideias salvas" } else { if (notes.size == 1) "nota salva" else "notas salvas" }}",
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.caption,
                )
            }
            OutlinedButton(
                onClick = onExport,
                enabled = notes.isNotEmpty(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.TextPrimary),
            ) {
                Icon(Icons.Outlined.Download, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Exportar .md")
            }
        }
        notice?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 8.dp),
                color = RockyColors.Accent,
                style = MaterialTheme.typography.caption,
            )
        }
        if (loadFailed) {
            OutlinedButton(onClick = onReload) { Text("Tentar carregar novamente") }
        }
        if (notes.isEmpty()) {
            Text(
                text = "As sugestões salvas durante a live aparecerão aqui.",
                modifier = Modifier.padding(vertical = 28.dp),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.body2,
            )
        } else {
            notes.forEachIndexed { index, note ->
                NoteRow(
                    note = note,
                    onEdit = { editingNote = note },
                    onDelete = { deletingNote = note },
                )
                if (index < notes.lastIndex) Divider(color = RockyColors.Divider)
            }
        }
    }
}
