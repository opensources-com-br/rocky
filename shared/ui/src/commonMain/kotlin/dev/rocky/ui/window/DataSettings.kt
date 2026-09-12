package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun DataSettings(
    notes: LocalNotesState,
    directory: String,
    buildLabel: String,
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
            }) { Text("Confirmar") } },
            dismissButton = { TextButton(onClick = { confirmation = null }) { Text("Cancelar") } },
        )
    }
    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Dados e privacidade", style = MaterialTheme.typography.h6)
        Text("Rocky $buildLabel", style = MaterialTheme.typography.caption)
        Text("Notas e fontes ficam no SQLite local. O chat é temporário. APIs de IA recebem as mensagens selecionadas; Ollama pode processá-las localmente.")
        Text("Chaves salvas usam Keychain no macOS ou DPAPI do usuário no Windows. Tokens Twitch ficam na memória.")
        Text(directory, style = MaterialTheme.typography.caption)
        OutlinedButton(onClick = { perform(onOpenDataDirectory, "Pasta de dados aberta.") }) { Text("Abrir pasta de dados") }
        OutlinedButton(onClick = onExportNotes, enabled = notes.notes.isNotEmpty()) { Text("Exportar notas antes de apagar") }
        OutlinedButton(onClick = { confirmation = "Apagar notas" }, enabled = notes.notes.isNotEmpty() && !notes.loadFailed) { Text("Apagar notas") }
        OutlinedButton(onClick = { confirmation = "Redefinir configurações" }) { Text("Redefinir configurações") }
        OutlinedButton(onClick = { confirmation = "Remover modelo de voz" }) { Text("Remover modelo de voz") }
        Text("A exclusão lógica não apaga cópias já exportadas, backups do sistema ou dados retidos pelo provedor de IA.", style = MaterialTheme.typography.caption)
        (notice ?: notes.notice)?.let { Text(it) }
    }
}
