package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.core.agent.AgentTone
import dev.rocky.ui.theme.RockyColors
import kotlin.math.roundToInt

@Composable
internal fun AgentSettings(agent: AgentState) {
    val configuration = agent.configuration
    val toneLabel = configuration.tone.label
    val frequency = (configuration.interventionsPerTenMinutes - 1) / 8f
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        SettingTitle("Nome do agente", "Como o chat e você chamam o assistente.")
        OutlinedTextField(
            value = configuration.name,
            onValueChange = agent::updateName,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(48.dp).testTag("agent-name-field"),
            singleLine = true,
            textStyle = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = RockyColors.TextPrimary,
                backgroundColor = RockyColors.SurfaceElevated,
                focusedBorderColor = RockyColors.Accent,
                unfocusedBorderColor = RockyColors.Border,
                cursorColor = RockyColors.Accent,
            ),
        )
        Spacer(Modifier.height(16.dp))
        SettingTitle("Tom de voz", "Define como o agente formula as intervenções.", toneLabel)
        ChoiceRow(AgentTone.entries.map { it.label }, toneLabel) { selected ->
            agent.updateTone(AgentTone.entries.first { it.label == selected })
        }
        Spacer(Modifier.height(16.dp))
        SettingTitle(
            "Frequência de fala",
            "Quantas vezes por 10 minutos ele pode intervir.",
            "${configuration.interventionsPerTenMinutes}×",
        )
        RockySlider(frequency) { value -> agent.updateFrequency((value * 8).roundToInt() + 1) }
        Spacer(Modifier.height(8.dp))
        SettingTitle(
            "Interrupção automática",
            "A entrada de voz atual funciona por clique e não interrompe o streamer.",
            "em breve",
        )
    }
}

private val AgentTone.label: String
    get() = when (this) {
        AgentTone.Direct -> "Direto"
        AgentTone.Energetic -> "Animado"
        AgentTone.Analytical -> "Analítico"
        AgentTone.Ironic -> "Irônico"
    }
