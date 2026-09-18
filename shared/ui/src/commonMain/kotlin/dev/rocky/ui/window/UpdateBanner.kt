package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun UpdateBanner(updates: UpdateState, download: UpdateDownloadState, blocked: Boolean,
    onOpenSettings: () -> Unit, onRestart: () -> Boolean) {
    val available = updates.available ?: return
    if (updates.dismissed && !download.busy && download.prepared == null) return
    val progress by download.progress.collectAsState()
    val latestBlocked by rememberUpdatedState(blocked)
    val latestRestart by rememberUpdatedState(onRestart)
    Column(Modifier.fillMaxWidth().testTag("update-banner").padding(horizontal = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Update available", "Atualização disponível") + " · ${download.prepared?.version ?: available.version}",
                modifier = Modifier.weight(1f), style = MaterialTheme.typography.caption, color = RockyColors.TextSecondary)
            TextButton(enabled = !download.opening && !download.restarting && !(blocked && download.prepared != null), onClick = {
                when {
                    !download.supported || download.busy -> onOpenSettings()
                    download.prepared != null -> download.install({ !latestBlocked }, { latestRestart() })
                    else -> download.download(available)
                }
            }, modifier = Modifier.testTag("update-banner-action")) {
                Text(when {
                    download.restarting || download.opening -> tr("Restarting…", "Reiniciando…")
                    download.busy -> "${(progress * 100).toInt()}%"
                    download.prepared != null -> tr("Update and restart", "Atualizar e reiniciar")
                    else -> tr("Update", "Atualizar")
                })
            }
            if (!download.busy && download.prepared == null) TextButton(onClick = { updates.dismissed = true }) {
                Text(tr("Later", "Depois"))
            }
