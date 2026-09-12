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
    onCreate: (String) -> Boolean = { false },
    title: String = "Notas locais",
    ideas: Boolean = false,
    loadFailed: Boolean = false,
    onReload: () -> Unit = {},
) {
    var search by remember { mutableStateOf("") }
    var session by remember { mutableStateOf<String?>(null) }
    var sessionsOpen by remember { mutableStateOf(false) }
    val visibleNotes = notes.filter {
        (session == null || it.sessionId == session) &&
            (it.text.contains(search, true) || it.tag.contains(search, true))
    }
    var creating by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }
    if (creating) androidx.compose.material.AlertDialog(
        onDismissRequest = { creating = false },
        title = { Text(if (ideas) tr("New idea", "Nova ideia") else tr("New note", "Nova nota")) },
        text = { androidx.compose.material.OutlinedTextField(value = draft, onValueChange = { draft = it.take(2000) },
            label = { Text(tr("Text", "Texto")) }) },
        confirmButton = { androidx.compose.material.TextButton(enabled = draft.isNotBlank(),
            onClick = { if (onCreate(draft.trim())) { creating = false; draft = "" } }) { Text(tr("Save", "Salvar")) } },
        dismissButton = { androidx.compose.material.TextButton(onClick = { creating = false }) { Text(tr("Cancel", "Cancelar")) } },
    )
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
            androidx.compose.material.TextButton(onClick = { creating = true }) { Text(tr("New", "Nova")) }
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
        androidx.compose.material.OutlinedTextField(value = search, onValueChange = { search = it.take(200) },
            label = { Text(tr("Search records", "Buscar registros")) }, modifier = Modifier.fillMaxWidth())
        androidx.compose.foundation.layout.Box {
            androidx.compose.material.TextButton(onClick = { sessionsOpen = true }) {
                Text(session?.let { id -> notes.firstOrNull { it.sessionId == id }?.sessionLabel?.ifBlank { "Sem live" } } ?: tr("All lives", "Todas as lives"))
            }
            androidx.compose.material.DropdownMenu(sessionsOpen, { sessionsOpen = false }) {
                androidx.compose.material.DropdownMenuItem(onClick = { session = null; sessionsOpen = false }) { Text(tr("All lives", "Todas as lives")) }
                notes.distinctBy { it.sessionId }.forEach { note ->
                    androidx.compose.material.DropdownMenuItem(onClick = { session = note.sessionId; sessionsOpen = false }) {
                        Text(note.sessionLabel.ifBlank { tr("Without a live", "Sem live") })
                    }
                }
            }
        }
        if (visibleNotes.isEmpty()) {
            Text(
                text = "As sugestões salvas durante a live aparecerão aqui.",
                modifier = Modifier.padding(vertical = 28.dp),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.body2,
            )
        } else {
            visibleNotes.forEachIndexed { index, note ->
                NoteRow(
                    note = note,
                    onEdit = { editingNote = note },
                    onDelete = { deletingNote = note },
                )
                if (index < visibleNotes.lastIndex) Divider(color = RockyColors.Divider)
            }
        }
    }
}
