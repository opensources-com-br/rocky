package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.core.agent.AgentTone
import dev.rocky.core.locale.RockyLanguage
import dev.rocky.ui.theme.RockyColors
import kotlin.math.roundToInt

@Composable
internal fun AgentSettings(
    agent: AgentState,
    language: RockyLanguage,
    onLanguageChange: (RockyLanguage) -> Unit,
) {
    val configuration = agent.configuration
    val toneLabels = AgentTone.entries.associateWith { it.localizedLabel }
    val toneLabel = toneLabels.getValue(configuration.tone)
    val frequency = (configuration.interventionsPerTenMinutes - 1) / 8f
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        SettingTitle(tr("Language", "Idioma"), tr("Language used by the Rocky interface.", "Idioma usado pela interface do Rocky."))
        ChoiceRow(
            options = listOf("English", "Português (Brasil)"),
            selected = if (language == RockyLanguage.English) "English" else "Português (Brasil)",
        ) { selected ->
            onLanguageChange(if (selected == "English") RockyLanguage.English else RockyLanguage.PortugueseBrazil)
        }
        Spacer(Modifier.height(16.dp))
        SettingTitle(tr("Agent name", "Nome do agente"), tr("How you and the chat address the assistant.", "Como o chat e você chamam o assistente."))
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
        SettingTitle(tr("Tone of voice", "Tom de voz"), tr("How the agent phrases its interventions.", "Define como o agente formula as intervenções."), toneLabel)
        ChoiceRow(toneLabels.values.toList(), toneLabel) { selected ->
            agent.updateTone(toneLabels.entries.first { it.value == selected }.key)
        }
        Spacer(Modifier.height(16.dp))
        SettingTitle(
            tr("Speaking frequency", "Frequência de fala"),
            tr("How many times it may intervene every 10 minutes.", "Quantas vezes por 10 minutos ele pode intervir."),
            "${configuration.interventionsPerTenMinutes}×",
        )
        RockySlider(frequency) { value -> agent.updateFrequency((value * 8).roundToInt() + 1) }
        Spacer(Modifier.height(8.dp))
        SettingTitle(
            tr("Automatic interruption", "Interrupção automática"),
            tr("Voice input currently works by click and does not interrupt the streamer.", "A entrada de voz atual funciona por clique e não interrompe o streamer."),
            tr("coming soon", "em breve"),
        )
    }
}

private val AgentTone.localizedLabel: String
    @Composable get() = when (this) {
        AgentTone.Direct -> tr("Direct", "Direto")
        AgentTone.Energetic -> tr("Energetic", "Animado")
        AgentTone.Analytical -> tr("Analytical", "Analítico")
        AgentTone.Ironic -> tr("Ironic", "Irônico")
    }
