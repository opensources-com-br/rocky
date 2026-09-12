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
import dev.rocky.core.youtube.YouTubeConfiguration
import dev.rocky.core.youtube.YouTubeConnectionPhase
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun YouTubeAccountSettings(
    initialConfiguration: YouTubeConfiguration,
    youtube: YouTubeLiveState,
    onConnect: (YouTubeConfiguration) -> Unit,
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
                Spacer(Modifier.size(9.dp).background(RockyColors.YouTube, CircleShape))
                Column(Modifier.padding(start = 11.dp).weight(1f)) {
                    Text("YouTube", color = RockyColors.TextPrimary)
                    Text(
                        youtube.statusText,
                        color = youtube.statusColor,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.testTag("youtube-status"),
                    )
                }
                TextButton(onClick = { onOpenBrowser(YOUTUBE_CREDENTIALS_URL) }) { Text("Criar app") }
            }
            Text(
                "Ative a YouTube Data API v3 e crie credenciais OAuth do tipo aplicativo para computador.",
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 8.dp),
            )
            OutlinedTextField(
                draft.clientId,
                { draft = draft.copy(clientId = it) },
                Modifier.fillMaxWidth().padding(top = 10.dp).testTag("youtube-client-id"),
                label = { Text("Client ID") },
                singleLine = true,
                enabled = !youtube.phase.isConnecting,
            )
            OutlinedTextField(
                draft.clientSecret,
                { draft = draft.copy(clientSecret = it) },
                Modifier.fillMaxWidth().padding(top = 8.dp).testTag("youtube-client-secret"),
                label = { Text("Client Secret") },
                singleLine = true,
                enabled = !youtube.phase.isConnecting,
                visualTransformation = if (secretVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { CredentialVisibilityButton(secretVisible) { secretVisible = !secretVisible } },
            )
            Text(
                "Callback local: ${draft.redirectUri}",
                color = RockyColors.TextMuted,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 8.dp),
            )
            Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                if (youtube.phase in setOf(YouTubeConnectionPhase.Disconnected, YouTubeConnectionPhase.Failed)) {
                    PrimaryButton("Conectar YouTube", draft.isValid) { onConnect(draft) }
                } else {
                    OutlinedButton(onClick = onDisconnect, border = BorderStroke(1.dp, RockyColors.Border)) {
                        Text("Desconectar")
                    }
                }
                youtube.authorizationUri?.let { uri ->
                    OutlinedButton(
                        onClick = { onOpenBrowser(uri) },
                        modifier = Modifier.padding(start = 8.dp).testTag("youtube-open-browser"),
                        border = BorderStroke(1.dp, RockyColors.AccentMuted),
                    ) { Text("Abrir YouTube") }
                }
            }
        }
    }
}

private const val YOUTUBE_CREDENTIALS_URL = "https://console.cloud.google.com/apis/credentials"
private val YouTubeConfiguration.isValid get() = clientId.isNotBlank() && clientSecret.isNotBlank() &&
    (redirectUri.startsWith("http://127.0.0.1:") || redirectUri.startsWith("http://localhost:"))
private val YouTubeConnectionPhase.isConnecting get() = this in setOf(
    YouTubeConnectionPhase.Authenticating,
    YouTubeConnectionPhase.AwaitingAuthorization,
    YouTubeConnectionPhase.FindingBroadcast,
)
private val YouTubeLiveState.statusText get() = detail ?: when (phase) {
    YouTubeConnectionPhase.Disconnected -> "Não conectada"
    YouTubeConnectionPhase.Authenticating -> "Preparando autorização"
    YouTubeConnectionPhase.AwaitingAuthorization -> "Aguardando autorização"
    YouTubeConnectionPhase.FindingBroadcast -> "Procurando live ativa"
    YouTubeConnectionPhase.Connected -> "Conectada"
    YouTubeConnectionPhase.Failed -> "Falha na conexão"
}
private val YouTubeLiveState.statusColor get() = when (phase) {
    YouTubeConnectionPhase.Connected -> RockyColors.YouTube
    YouTubeConnectionPhase.Failed -> RockyColors.YouTube
    else -> RockyColors.TextMuted
}
