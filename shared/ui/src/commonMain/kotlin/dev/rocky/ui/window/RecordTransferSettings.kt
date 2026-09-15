package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors
import dev.rocky.core.live.LiveNote

@Composable
internal fun RecordTransferSettings(records: LocalNotesState, onBackup: (List<LiveNote>) -> Boolean,
    onChooseImport: () -> List<LiveNote>?) {
    val backupSaved = tr("Backup saved.", "Backup salvo.")
    val backupFailed = tr("Could not export backup.", "Não foi possível exportar o backup.")
    val importFailed = tr("Invalid or unsupported backup.", "Backup inválido ou incompatível.")
    var pending by remember { mutableStateOf<List<LiveNote>?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    SettingsPreferenceGroup(tr("Backups", "Backups")) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(tr("Backup includes saved records and their sources, not credentials.", "O backup inclui registros salvos e suas fontes, sem credenciais."),
                color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
            OutlinedButton(shape = RoundedCornerShape(8.dp), onClick = {
                runCatching { onBackup(records.notes.toList()) }.onSuccess { if (it) notice = backupSaved }
                    .onFailure { notice = backupFailed }
            }) { Text(tr("Export backup", "Exportar backup")) }
            OutlinedButton(shape = RoundedCornerShape(8.dp), onClick = {
                runCatching(onChooseImport).onSuccess { pending = it }.onFailure { notice = importFailed }
            }) { Text(tr("Import backup", "Importar backup")) }
            notice?.let { Text(it, color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption) }
        }
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
