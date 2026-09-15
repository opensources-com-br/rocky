package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.core.agent.AgentTone
import dev.rocky.core.locale.RockyLanguage
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun AgentSettings(
    agent: AgentState,
    language: RockyLanguage,
    onLanguageChange: (RockyLanguage) -> Unit,
) {
    val configuration = agent.configuration
    val toneLabels = AgentTone.entries.associateWith { it.localizedLabel }
    val toneLabel = toneLabels.getValue(configuration.tone)
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        SettingsPreferenceGroup(tr("Identity", "Identidade")) {
            val nameLabel = tr("Agent name", "Nome do agente")
            SettingsPreferenceRow(
                title = nameLabel,
                description = tr("How you and the chat address the assistant.", "Como você e o chat chamam o assistente."),
                stackWhenCompact = true,
            ) {
                OutlinedTextField(
                    value = configuration.name,
                    onValueChange = agent::updateName,
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("agent-name-field")
                        .semantics { contentDescription = nameLabel },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Medium),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = RockyColors.TextPrimary,
                        backgroundColor = RockyColors.Surface,
                        focusedBorderColor = RockyColors.Accent,
                        unfocusedBorderColor = RockyColors.Border,
                        cursorColor = RockyColors.Accent,
                    ),
                )
            }
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            SettingsPreferenceRow(tr("Language", "Idioma")) {
                SettingsPreferenceMenu(
                    options = listOf("English", "Português (Brasil)"),
                    selected = if (language == RockyLanguage.English) "English" else "Português (Brasil)",
                    tag = "agent-language-menu",
                ) { selected ->
                    onLanguageChange(if (selected == "English") RockyLanguage.English else RockyLanguage.PortugueseBrazil)
                }
            }
        }
        SettingsPreferenceGroup(tr("Communication", "Comunicação")) {
            SettingsPreferenceRow(tr("Tone of voice", "Tom de voz")) {
                SettingsPreferenceMenu(toneLabels.values.toList(), toneLabel, "agent-tone-menu") { selected ->
                    agent.updateTone(toneLabels.entries.first { it.value == selected }.key)
                }
            }
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            Text(
                text = configuration.tone.localizedDescription,
                modifier = Modifier.padding(16.dp),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.body2,
            )
        }
        Text(
            text = tr("Choose on-demand, discreet or proactive interventions in AI settings.",
                "Escolha intervenções sob demanda, discretas ou proativas em IA."),
            modifier = Modifier.padding(horizontal = 4.dp),
            color = RockyColors.TextSecondary,
            style = MaterialTheme.typography.caption,
        )
        Surface(color = RockyColors.SurfaceElevated, shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tr("Automatic interruption", "Interrupção automática"), style = MaterialTheme.typography.body2)
                    Spacer(Modifier.weight(1f))
                    Surface(color = RockyColors.SurfaceSelected, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            tr("Coming soon", "Em breve"),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = RockyColors.TextSecondary,
                            style = MaterialTheme.typography.caption,
                        )
                    }
                }
                Text(
                    text = tr("Voice input currently works by click and does not interrupt the streamer.",
                        "A entrada de voz atual funciona por clique e não interrompe o streamer."),
                    modifier = Modifier.padding(top = 8.dp),
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.caption,
                )
            }
        }
    }
}

private val AgentTone.localizedDescription: String
    @Composable get() = when (this) {
        AgentTone.Direct -> tr("Clear, concise responses that get straight to the point.", "Respostas claras e objetivas, direto ao ponto.")
        AgentTone.Energetic -> tr("Lively responses with enthusiasm and energy.", "Respostas leves, com entusiasmo e energia.")
        AgentTone.Analytical -> tr("Thoughtful responses with context and careful reasoning.", "Respostas ponderadas, com contexto e raciocínio cuidadoso.")
        AgentTone.Ironic -> tr("Witty responses with a touch of irony.", "Respostas bem-humoradas, com um toque de ironia.")
    }

private val AgentTone.localizedLabel: String
    @Composable get() = when (this) {
        AgentTone.Direct -> tr("Direct", "Direto")
        AgentTone.Energetic -> tr("Energetic", "Animado")
        AgentTone.Analytical -> tr("Analytical", "Analítico")
        AgentTone.Ironic -> tr("Ironic", "Irônico")
    }
