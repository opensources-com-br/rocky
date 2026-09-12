package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.kick.KickConnectionPhase
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun KickAccountSettings(
    initialConfiguration: KickConfiguration,
    kick: KickLiveState,
    onConnect: (KickConfiguration) -> Unit,
    onDisconnect: () -> Unit,
    onOpenBrowser: (String) -> Unit,
) {
    var draft by remember(initialConfiguration) { mutableStateOf(initialConfiguration) }
    var secretVisible by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        color = RockyColors.SurfaceElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, RockyColors.Border),
    ) {
        Column(Modifier.padding(13.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.size(9.dp).background(RockyColors.Kick, CircleShape))
                Column(Modifier.padding(start = 11.dp).weight(1f)) {
                    Text("Kick", color = RockyColors.TextPrimary)
                    Text(kick.statusText, color = kick.statusColor,
                        style = MaterialTheme.typography.caption, modifier = Modifier.testTag("kick-status"))
                }
                TextButton(onClick = { onOpenBrowser("https://dev.kick.com") }) { Text("Criar app") }
            }
            Text(
                "Callback OAuth: ${draft.redirectUri}. Para receber o chat, cadastre no Kick Dev uma URL pública HTTPS que encaminhe para ${draft.redirectUri.substringBefore("/oauth")}/webhooks/kick.",
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 8.dp),
            )
            OutlinedTextField(draft.clientId, { draft = draft.copy(clientId = it) },
                Modifier.fillMaxWidth().padding(top = 10.dp).testTag("kick-client-id"),
                label = { Text("Client ID") }, singleLine = true, enabled = !kick.phase.isConnecting)
            OutlinedTextField(draft.clientSecret, { draft = draft.copy(clientSecret = it) },
                Modifier.fillMaxWidth().padding(top = 8.dp).testTag("kick-client-secret"),
                label = { Text("Client Secret") }, singleLine = true, enabled = !kick.phase.isConnecting,
                visualTransformation = if (secretVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { CredentialVisibilityButton(secretVisible) { secretVisible = !secretVisible } })
            OutlinedTextField(draft.redirectUri, { draft = draft.copy(redirectUri = it) },
                Modifier.fillMaxWidth().padding(top = 8.dp).testTag("kick-redirect-uri"),
                label = { Text("Callback local") }, singleLine = true, enabled = !kick.phase.isConnecting)
            Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                if (kick.phase in setOf(KickConnectionPhase.Disconnected, KickConnectionPhase.Failed)) {
                    PrimaryButton("Conectar Kick", draft.isValid) { onConnect(draft) }
                } else {
                    OutlinedButton(onClick = onDisconnect,
                        border = BorderStroke(1.dp, RockyColors.Border)) { Text("Desconectar") }
                }
                kick.authorizationUri?.let { uri ->
                    OutlinedButton(onClick = { onOpenBrowser(uri) },
                        Modifier.padding(start = 8.dp).testTag("kick-open-browser"),
                        border = BorderStroke(1.dp, RockyColors.AccentMuted)) { Text("Abrir Kick") }
                }
            }
        }
    }
}

private val KickConfiguration.isValid get() = clientId.isNotBlank() && clientSecret.isNotBlank() &&
    redirectUri.startsWith("http://localhost:")
private val KickConnectionPhase.isConnecting get() = this in setOf(
    KickConnectionPhase.Authenticating, KickConnectionPhase.AwaitingAuthorization, KickConnectionPhase.Connecting)
private val KickLiveState.statusText get() = detail ?: when (phase) {
    KickConnectionPhase.Disconnected -> "Não conectada"
    KickConnectionPhase.Authenticating -> "Preparando autorização"
    KickConnectionPhase.AwaitingAuthorization -> "Aguardando autorização"
    KickConnectionPhase.Connecting -> "Conectando ao chat"
    KickConnectionPhase.Connected -> "Conectada"
    KickConnectionPhase.Failed -> "Falha na conexão"
}
private val KickLiveState.statusColor get() = when (phase) {
    KickConnectionPhase.Connected -> RockyColors.Kick
    KickConnectionPhase.Failed -> RockyColors.YouTube
    else -> RockyColors.TextMuted
}
