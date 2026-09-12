package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import dev.rocky.core.live.LiveNote

@Composable
internal fun RecordTransferSettings(records: LocalNotesState, onBackup: (List<LiveNote>) -> Boolean,
    onChooseImport: () -> List<LiveNote>?) {
    var pending by remember { mutableStateOf<List<LiveNote>?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    Column {
        Text(tr("Backup includes saved records and their sources, not credentials.", "O backup inclui registros salvos e suas fontes, sem credenciais."))
        OutlinedButton(onClick = {
            runCatching { onBackup(records.notes.toList()) }.onSuccess { if (it) notice = tr("Backup saved.", "Backup salvo.") }
                .onFailure { notice = tr("Could not export backup.", "Não foi possível exportar o backup.") }
        }) { Text(tr("Export backup", "Exportar backup")) }
        OutlinedButton(onClick = {
            runCatching(onChooseImport).onSuccess { pending = it }.onFailure { notice = tr("Invalid or unsupported backup.", "Backup inválido ou incompatível.") }
        }) { Text(tr("Import backup", "Importar backup")) }
        notice?.let { Text(it) }
    }
    pending?.let { incoming ->
        AlertDialog(onDismissRequest = { pending = null }, title = { Text(tr("Import records?", "Importar registros?")) },
            text = { Column {
                Text("${incoming.size} " + tr("records. Existing records are preserved. Conflicting IDs cancel the import.", "registros. Registros existentes são preservados. IDs conflitantes cancelam a importação."))
                records.notice?.let { Text(it) }
            } },
            confirmButton = { TextButton(onClick = { if (records.importRecords(incoming)) { pending = null; notice = records.notice } }) { Text(tr("Import", "Importar")) } },
            dismissButton = { TextButton(onClick = { pending = null }) { Text(tr("Cancel", "Cancelar")) } })
    }
}
