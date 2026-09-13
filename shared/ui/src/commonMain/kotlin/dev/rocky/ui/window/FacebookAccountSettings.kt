package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.rocky.core.facebook.FacebookConfiguration
import dev.rocky.core.facebook.FacebookConnectionPhase
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun FacebookAccountSettings(
    initialConfiguration: FacebookConfiguration,
    facebook: FacebookLiveState,
    onConnect: (FacebookConfiguration) -> Unit,
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
                Spacer(Modifier.size(9.dp).background(RockyColors.Facebook, CircleShape))
                Column(Modifier.padding(start = 11.dp).weight(1f)) {
                    Text("Facebook", color = RockyColors.TextPrimary)
                    Text(
                        facebook.statusText,
                        color = facebook.statusColor,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.testTag("facebook-status"),
                    )
                }
                TextButton(onClick = { onOpenBrowser(FACEBOOK_APPS_URL) }) { Text("Criar app") }
            }
            Text(
                "Crie um app Business, adicione Login do Facebook e permita leitura das Páginas.",
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 8.dp),
            )
            OutlinedTextField(
                draft.appId,
                { draft = draft.copy(appId = it) },
                Modifier.fillMaxWidth().padding(top = 10.dp).testTag("facebook-app-id"),
                label = { Text("App ID") },
                singleLine = true,
                enabled = !facebook.phase.isConnecting,
            )
            OutlinedTextField(
                draft.appSecret,
                { draft = draft.copy(appSecret = it) },
                Modifier.fillMaxWidth().padding(top = 8.dp).testTag("facebook-app-secret"),
                label = { Text("App Secret") },
                singleLine = true,
                enabled = !facebook.phase.isConnecting,
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
                if (facebook.phase in setOf(FacebookConnectionPhase.Disconnected, FacebookConnectionPhase.Failed)) {
                    PrimaryButton("Conectar Facebook", draft.isValid) { onConnect(draft) }
                } else {
                    OutlinedButton(onClick = onDisconnect, border = BorderStroke(1.dp, RockyColors.Border)) {
                        Text("Desconectar")
                    }
                }
                facebook.authorizationUri?.let { uri ->
                    OutlinedButton(
                        onClick = { onOpenBrowser(uri) },
                        modifier = Modifier.padding(start = 8.dp).testTag("facebook-open-browser"),
                        border = BorderStroke(1.dp, RockyColors.AccentMuted),
                    ) { Text("Abrir Facebook") }
                }
            }
        }
    }
}

private const val FACEBOOK_APPS_URL = "https://developers.facebook.com/apps/"
private val FacebookConfiguration.isValid get() = appId.isNotBlank() && appSecret.isNotBlank() &&
    (redirectUri.startsWith("http://127.0.0.1:") || redirectUri.startsWith("http://localhost:"))
private val FacebookConnectionPhase.isConnecting get() = this in setOf(
    FacebookConnectionPhase.Authenticating,
    FacebookConnectionPhase.AwaitingAuthorization,
    FacebookConnectionPhase.FindingLive,
)

private val FacebookLiveState.statusText get() = detail ?: when (phase) {
    FacebookConnectionPhase.Disconnected -> "Não conectada"
    FacebookConnectionPhase.Authenticating -> "Preparando autorização"
    FacebookConnectionPhase.AwaitingAuthorization -> "Aguardando autorização"
    FacebookConnectionPhase.FindingLive -> "Procurando live ativa"
    FacebookConnectionPhase.Connected -> "Conectada"
    FacebookConnectionPhase.Failed -> "Falha na conexão"
}
private val FacebookLiveState.statusColor get() = when (phase) {
    FacebookConnectionPhase.Connected, FacebookConnectionPhase.Failed -> RockyColors.Facebook
    else -> RockyColors.TextMuted
}
