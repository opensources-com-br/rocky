package dev.rocky.ui.window

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import dev.rocky.core.updates.AvailableUpdate

@Composable
internal fun UpdateDownloadSettings(state: UpdateDownloadState, available: AvailableUpdate?, blocked: Boolean,
    onRestart: () -> Boolean = { false }) {
    val progress by state.progress.collectAsState()
    val latestBlocked by rememberUpdatedState(blocked)
    val latestRestart by rememberUpdatedState(onRestart)
    state.installationNotice?.let { Text(updateInstallationMessage(it)) }
    state.unavailableReason?.let { Text(updateUnavailableMessage(it)) }
    if (!state.supported) return
    when {
        state.restarting -> Text(tr("Restarting Rocky…", "Reiniciando o Rocky…"))
        state.opening -> {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text(tr("Preparing update and restart…", "Preparando atualização e reinício…"))
        }
        state.busy -> {
            LinearProgressIndicator(progress, Modifier.fillMaxWidth())
            Text("${(progress * 100).toInt()}% · " + tr("Downloading and verifying update", "Baixando e verificando atualização"))
            TextButton(onClick = state::cancel, modifier = Modifier.testTag("update-cancel")) { Text(tr("Cancel", "Cancelar")) }
        }
        state.prepared != null -> {
            Text("${tr("Ready to install", "Pronto para instalar")}: ${state.prepared?.version}")
            Button(enabled = !blocked, modifier = Modifier.testTag("update-restart"), onClick = {
                state.install({ !latestBlocked }, { latestRestart() })
            }) { Text(tr("Update and restart", "Atualizar e reiniciar")) }
            Text(tr("Rocky will restart automatically. Your notes, settings and voice models will be preserved.",
                "O Rocky reiniciará automaticamente. Suas notas, configurações e modelos de voz serão preservados."))
        }
        available != null -> OutlinedButton(modifier = Modifier.testTag("update-download"), onClick = { state.download(available) }) {
            Text(if (state.notice == UpdateDownloadNotice.DownloadFailed) tr("Retry download", "Tentar baixar novamente")
                else tr("Download update", "Baixar atualização"))
        }
    }
    if (blocked) Text(tr("Disconnect your platforms before restarting to update.", "Desconecte suas plataformas antes de reiniciar para atualizar."))
    state.notice?.let { Text(updateDownloadMessage(it), Modifier.testTag("update-notice")) }
        dismissButton = { TextButton(onClick = { confirm = false }) { Text(tr("Later", "Depois")) } },
    )
}
