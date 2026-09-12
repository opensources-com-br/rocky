package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun FirstUseContent(
    twitchConnected: Boolean,
    aiVerified: Boolean,
    voiceVerified: Boolean,
    onConfigureTwitch: () -> Unit,
    onConfigureAi: () -> Unit,
    onConfigureVoice: () -> Unit,
    onComplete: () -> Unit,
) {
    val ready = firstUseReady(twitchConnected, aiVerified, voiceVerified)
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp)) {
        Text("Configure o Rocky", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
        Text(
            text = "Conecte a Twitch e a IA para começar por texto. Ative a voz quando quiser.",
            modifier = Modifier.padding(top = 5.dp, bottom = 14.dp),
            color = RockyColors.TextSecondary,
            style = MaterialTheme.typography.body2,
        )
        SetupStep(
            number = 1,
            title = "Conecte sua Twitch",
            description = "Informe o Client ID de um aplicativo público e autorize o canal que fará a transmissão.",
            complete = twitchConnected,
            action = if (twitchConnected) "Revisar Twitch" else "Configurar Twitch",
            onAction = onConfigureTwitch,
        )
        SetupStep(
            number = 2,
            title = "Configure a IA",
            description = "Escolha Ollama local, OpenAI API ou OpenRouter e use “Testar conexão” antes da live.",
            complete = aiVerified,
            action = if (aiVerified) "Revisar IA" else "Configurar IA",
            onAction = onConfigureAi,
        )
        SetupStep(
            number = 3,
            title = "Teste a voz (opcional)",
            description = "Configure o whisper.cpp e o microfone, depois confirme o áudio com “Testar voz”.",
            complete = voiceVerified,
            optional = true,
            action = if (voiceVerified) "Revisar voz" else "Configurar voz",
            onAction = onConfigureVoice,
        )
        Text(
            text = "Rocky não exige uma conta própria. As notas ficam no computador. Chaves de IA salvas usam o cofre do sistema; tokens Twitch ficam na memória.",
            modifier = Modifier.padding(vertical = 12.dp),
            color = RockyColors.TextMuted,
            style = MaterialTheme.typography.caption,
        )
        Button(
            modifier = Modifier.fillMaxWidth().height(42.dp),
            onClick = onComplete,
            enabled = ready,
            colors = ButtonDefaults.buttonColors(backgroundColor = RockyColors.Accent, contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text("Concluir configuração", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SetupStep(
    number: Int,
    title: String,
    description: String,
    complete: Boolean,
    optional: Boolean = false,
    action: String,
    onAction: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
        color = RockyColors.SurfaceElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (complete) RockyColors.AccentMuted else RockyColors.Border),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$number. $title",
                    modifier = Modifier.weight(1f),
                    color = RockyColors.TextPrimary,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = when {
                        complete -> "pronto"
                        optional -> "opcional"
                        else -> "pendente"
                    },
                    color = if (complete) RockyColors.Accent else RockyColors.TextMuted,
                    style = MaterialTheme.typography.caption,
                )
            }
            Text(
                text = description,
                modifier = Modifier.padding(top = 4.dp),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onAction,
                border = BorderStroke(1.dp, RockyColors.Border),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.TextPrimary),
            ) {
                Text(action)
            }
        }
    }
}

internal fun firstUseReady(twitchConnected: Boolean, aiVerified: Boolean, voiceVerified: Boolean): Boolean =
    twitchConnected && aiVerified
