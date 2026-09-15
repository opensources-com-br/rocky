package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun DataSettings(
    notes: LocalNotesState,
    directory: String,
    buildLabel: String,
    onBackup: (List<dev.rocky.core.live.LiveNote>) -> Boolean = { false },
    onChooseImport: () -> List<dev.rocky.core.live.LiveNote>? = { null },
    updates: UpdateState = remember { UpdateState { null } },
    updateDownload: UpdateDownloadState = remember { UpdateDownloadState(null) },
    updateBlocked: Boolean = false,
    diagnosticReport: () -> String = { "" },
    onExportDiagnostic: (String) -> Boolean = { false },
    onOpenGuide: (String) -> Unit = {},
    checkUpdatesOnStart: Boolean = false,
    onCheckUpdatesOnStart: (Boolean) -> Unit = {},
    onOpenDataDirectory: () -> Unit,
    onExportNotes: () -> Unit,
    onResetSettings: () -> Unit,
    onRemoveModel: () -> Unit,
) {
    var confirmation by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    fun perform(action: () -> Unit, success: String) {
        notice = runCatching(action).fold({ success }, { "Não foi possível concluir. Verifique o acesso ao cofre ou à pasta de dados e tente novamente." })
    }
    confirmation?.let { action ->
        AlertDialog(
            modifier = Modifier.testTag("data-confirmation"),
            onDismissRequest = { confirmation = null },
            title = { Text(action) },
            text = { Text(if (action == "Apagar notas") "Esta ação apaga todas as notas e suas fontes. Exporte antes de continuar."
                else if (action == "Remover modelo de voz") "O ouvinte será interrompido e apenas o modelo gerenciado será removido. Notas e modelos externos serão preservados."
                else "A sessão será interrompida. Reabra o Rocky após concluir. Notas e exportações serão preservadas.") },
            confirmButton = { TextButton(onClick = {
                when (action) {
                    "Apagar notas" -> { notes.deleteAll(); notice = notes.notice }
                    "Redefinir configurações" -> perform(onResetSettings, "Configurações e chave removidas. Reabra o Rocky.")
                    else -> perform(onRemoveModel, "Modelo gerenciado removido. Modelos externos foram preservados.")
                }
                confirmation = null
            }) { Text("Confirmar", color = RockyColors.WindowClose) } },
            dismissButton = { TextButton(onClick = { confirmation = null }) { Text("Cancelar") } },
        )
    }
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)) {
        SettingsPreferenceGroup(tr("Privacy and storage", "Privacidade e armazenamento")) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Rocky $buildLabel", color = RockyColors.TextPrimary, style = MaterialTheme.typography.body2)
                Text("Registros, perguntas agrupadas e suas fontes ficam no SQLite local. O restante do chat é temporário. APIs de IA recebem o contexto selecionado; Ollama pode processá-lo localmente.",
                    color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
                Text("Chaves salvas usam Keychain no macOS ou DPAPI do usuário no Windows. Tokens Twitch ficam na memória.",
                    color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
                Divider(color = RockyColors.Border)
                Text(directory, color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
                OutlinedButton(shape = RoundedCornerShape(8.dp),
                    onClick = { perform(onOpenDataDirectory, "Pasta de dados aberta.") }) { Text("Abrir pasta de dados") }
            }
        }
        RecordTransferSettings(notes, onBackup, onChooseImport)
        MaintenanceSettings(updates, diagnosticReport, onExportDiagnostic, onOpenGuide) {
            UpdateDownloadSettings(updateDownload, updates.available, updateBlocked)
            Divider(color = RockyColors.Border)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tr("Check updates when opening Rocky", "Verificar atualizações ao abrir Rocky"),
                    modifier = Modifier.weight(1f), style = MaterialTheme.typography.body2)
                Switch(checkUpdatesOnStart, onCheckUpdatesOnStart, Modifier.testTag("data-startup-updates"),
                    colors = SwitchDefaults.colors(checkedThumbColor = RockyColors.TextPrimary,
                        checkedTrackColor = RockyColors.Accent))
            }
        }
        SettingsPreferenceGroup(tr("Data management", "Gerenciamento de dados")) {
            DataActionRow(tr("Saved notes", "Notas salvas"), tr("Export a copy before deleting.", "Exporte uma cópia antes de apagar."),
                "Exportar notas antes de apagar", notes.notes.isNotEmpty(), false, onExportNotes)
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            DataActionRow(tr("Delete notes", "Exclusão de notas"), tr("Remove saved notes and their sources.", "Remove as notas salvas e suas fontes."),
                "Apagar notas", notes.notes.isNotEmpty() && !notes.loadFailed, true) { confirmation = "Apagar notas" }
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            DataActionRow(tr("Settings", "Configurações"), tr("Reset preferences and saved credentials. Notes are preserved.", "Redefine preferências e credenciais salvas. As notas são preservadas."),
                "Redefinir configurações", true, true) { confirmation = "Redefinir configurações" }
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            DataActionRow(tr("Managed voice model", "Modelo de voz gerenciado"), tr("Remove only Rocky's downloaded model, not external models.", "Remove apenas o modelo baixado pelo Rocky, não modelos externos."),
                "Remover modelo de voz", true, true) { confirmation = "Remover modelo de voz" }
        }
        Text("A exclusão lógica não apaga cópias já exportadas, backups do sistema ou dados retidos pelo provedor de IA.",
            color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
        (notice ?: notes.notice)?.let { Text(it, Modifier.testTag("data-notice"),
            color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption) }
    }
}

@Composable
private fun DataActionRow(title: String, description: String, label: String, enabled: Boolean,
    destructive: Boolean, onClick: () -> Unit) {
    SettingsPreferenceRow(title, description, stackWhenCompact = true) {
        OutlinedButton(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (destructive) RockyColors.WindowClose else RockyColors.TextPrimary)) {
            Text(label, style = MaterialTheme.typography.body2)
        }
    }
}
