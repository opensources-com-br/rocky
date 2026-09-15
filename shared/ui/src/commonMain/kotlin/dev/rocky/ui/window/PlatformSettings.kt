package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.rocky.core.twitch.TwitchConnectionPhase
import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.youtube.YouTubeConfiguration
import dev.rocky.core.facebook.FacebookConfiguration
import dev.rocky.core.tiktok.TikTokConfiguration
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun PlatformSettings(
    clientId: String,
    onClientIdChange: (String) -> Unit,
    twitch: TwitchLiveState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onOpenBrowser: (String) -> Unit,
    kickConfiguration: KickConfiguration = KickConfiguration(),
    kick: KickLiveState? = null,
    onConnectKick: (KickConfiguration) -> Unit = {},
    onDisconnectKick: () -> Unit = {},
    onOpenKickBrowser: (String) -> Unit = onOpenBrowser,
    youtubeConfiguration: YouTubeConfiguration = YouTubeConfiguration(),
    youtube: YouTubeLiveState? = null,
    onConnectYouTube: (YouTubeConfiguration) -> Unit = {},
    onDisconnectYouTube: () -> Unit = {},
    onOpenYouTubeBrowser: (String) -> Unit = onOpenBrowser,
    facebookConfiguration: FacebookConfiguration = FacebookConfiguration(),
    facebook: FacebookLiveState? = null,
    onConnectFacebook: (FacebookConfiguration) -> Unit = {},
    onDisconnectFacebook: () -> Unit = {},
    onOpenFacebookBrowser: (String) -> Unit = onOpenBrowser,
    tiktokConfiguration: TikTokConfiguration = TikTokConfiguration(),
    tiktok: TikTokLiveState? = null,
    onConnectTikTok: (TikTokConfiguration) -> Unit = {},
    onDisconnectTikTok: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(18.dp)) {
        TwitchAccount(clientId, onClientIdChange, twitch, onConnect, onDisconnect, onOpenBrowser)
        kick?.let { KickAccountSettings(kickConfiguration, it, onConnectKick, onDisconnectKick, onOpenKickBrowser) }
            ?: UpcomingPlatform("Kick", PlatformColor.Kick)
        youtube?.let {
            YouTubeAccountSettings(youtubeConfiguration, it, onConnectYouTube, onDisconnectYouTube, onOpenYouTubeBrowser)
        } ?: UpcomingPlatform("YouTube", PlatformColor.YouTube)
        facebook?.let {
            FacebookAccountSettings(facebookConfiguration, it, onConnectFacebook, onDisconnectFacebook, onOpenFacebookBrowser)
        } ?: UpcomingPlatform("Facebook", PlatformColor.Facebook)
        tiktok?.let {
            TikTokAccountSettings(tiktokConfiguration, it, onConnectTikTok, onDisconnectTikTok)
        } ?: UpcomingPlatform("TikTok", PlatformColor.TikTok)
    }
}

private const val TWITCH_APP_REGISTRATION_URL = "https://dev.twitch.tv/docs/authentication/register-app/"

@Composable
private fun TwitchAccount(
    clientId: String,
    onClientIdChange: (String) -> Unit,
    twitch: TwitchLiveState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onOpenAuthorization: (String) -> Unit,
) {
    var advanced by remember { mutableStateOf(clientId.isBlank()) }
    var clientIdVisible by remember { mutableStateOf(false) }

    PlatformPreferenceGroup("Twitch", twitch.statusText, twitch.statusColor, "twitch-status") {
        androidx.compose.material.TextButton(onClick = { advanced = !advanced }) {
            Text(tr("Advanced · Twitch app", "Avançado · aplicativo Twitch"))
        }
        if (advanced) Column {
            Text(
                text = if (clientId.isBlank()) {
                    "Este build não inclui um Client ID. Crie um aplicativo público na Twitch e cole o identificador abaixo."
                } else {
                    "O Client ID já está configurado. Conecte e autorize o canal que fará a transmissão."
                },
                modifier = Modifier.padding(top = 8.dp),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
            )
            OutlinedButton(
                onClick = { onOpenAuthorization(TWITCH_APP_REGISTRATION_URL) },
                modifier = Modifier.padding(top = 6.dp),
                border = BorderStroke(1.dp, RockyColors.Border),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.TextPrimary),
            ) { Text(if (clientId.isBlank()) "Criar Client ID" else "Sobre o Client ID") }
            OutlinedTextField(
                value = clientId,
                onValueChange = onClientIdChange,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).testTag("twitch-client-id"),
                label = { Text("Client ID") },
                placeholder = { Text("Cole o Client ID do seu aplicativo") },
                singleLine = true, shape = PlatformFieldShape,
                textStyle = MaterialTheme.typography.body2, colors = platformFieldColors(),
                enabled = !twitch.phase.isConnecting,
                visualTransformation = if (clientIdVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    CredentialVisibilityButton(clientIdVisible) { clientIdVisible = !clientIdVisible }
                },
            )
            Text(
                text = "O Client ID identifica seu aplicativo público da Twitch. Tokens ficam apenas na memória e são apagados ao desconectar ou fechar o Rocky.",
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 6.dp),
                color = RockyColors.TextMuted,
                style = MaterialTheme.typography.caption,
            )
        }
        twitch.userCode?.let { code ->
            Text(
                text = code,
                modifier = Modifier.padding(top = 12.dp).testTag("twitch-user-code"),
                color = RockyColors.TextPrimary,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Use este código para autorizar o Rocky na Twitch.",
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
            )
        }
        Row(modifier = Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (twitch.phase == TwitchConnectionPhase.Disconnected || twitch.phase == TwitchConnectionPhase.Failed) {
                PrimaryButton("Conectar Twitch", clientId.isNotBlank(), onConnect)
            } else {
                OutlinedButton(
                    onClick = onDisconnect,
                    border = BorderStroke(1.dp, RockyColors.Border),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.TextPrimary),
                ) { Text("Desconectar") }
            }
            twitch.verificationUri?.let { uri ->
                OutlinedButton(
                    onClick = { onOpenAuthorization(uri) },
                    modifier = Modifier.padding(start = 8.dp).testTag("twitch-open-browser"),
                    border = BorderStroke(1.dp, RockyColors.AccentMuted),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.Accent),
                ) { Text("Abrir Twitch") }
            }
        }
    }
}

@Composable
internal fun PrimaryButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = PlatformFieldShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = RockyColors.Accent, contentColor = Color.Black),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp),
    ) { Text(label, fontWeight = FontWeight.Bold) }
}

@Composable
private fun UpcomingPlatform(name: String, color: PlatformColor) {
    SettingsPreferenceGroup(name) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Coming soon", "Em breve"), color = color.color(), style = MaterialTheme.typography.caption)
        }
    }
}

private val TwitchConnectionPhase.isConnecting: Boolean
    get() = this in setOf(
        TwitchConnectionPhase.Authenticating,
        TwitchConnectionPhase.AwaitingAuthorization,
        TwitchConnectionPhase.Connecting,
        TwitchConnectionPhase.Reconnecting,
    )

private val TwitchLiveState.statusText: String
    get() = detail ?: when (phase) {
        TwitchConnectionPhase.Disconnected -> "Não conectada"
        TwitchConnectionPhase.Authenticating -> "Iniciando autenticação"
        TwitchConnectionPhase.AwaitingAuthorization -> "Aguardando autorização"
        TwitchConnectionPhase.Connecting -> "Conectando ao chat"
        TwitchConnectionPhase.Connected -> "Conectada"
        TwitchConnectionPhase.Reconnecting -> "Reconectando"
        TwitchConnectionPhase.Failed -> "Falha na conexão"
    }

private val TwitchLiveState.statusColor
    get() = when (phase) {
        TwitchConnectionPhase.Connected -> RockyColors.Twitch
        TwitchConnectionPhase.Failed -> RockyColors.YouTube
        else -> RockyColors.TextMuted
    }

private fun PlatformColor.color(): Color = when (this) {
    PlatformColor.Twitch -> RockyColors.Twitch
    PlatformColor.Kick -> RockyColors.Kick
    PlatformColor.YouTube -> RockyColors.YouTube
    PlatformColor.Facebook -> RockyColors.Facebook
    PlatformColor.TikTok -> RockyColors.TikTok
    PlatformColor.Offline -> RockyColors.TextMuted
}
