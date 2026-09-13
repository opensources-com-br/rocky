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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
            }
        }
    }
}

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
