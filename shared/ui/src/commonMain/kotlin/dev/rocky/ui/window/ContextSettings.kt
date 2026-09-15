package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.rocky.core.agent.InterventionProfile
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun ContextSettings(ai: AiSuggestionState) {
    var open by remember { mutableStateOf(false) }
    SettingsPreferenceGroup(tr("Behavior and context", "Comportamento e contexto")) {
        val labels = listOf(tr("On demand", "Sob demanda"), tr("Discreet", "Discreto"), tr("Proactive", "Proativo"))
        SettingsPreferenceRow(tr("Intervention profile", "Perfil de intervenção")) {
            SettingsPreferenceMenu(labels, labels[ai.profile.ordinal], "ai-profile-menu") {
                ai.updateProfile(InterventionProfile.entries[labels.indexOf(it)])
            }
        }
        Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(tr("Automatic limits: off / once per 5 minutes / once per 2 minutes. Direct questions take priority.",
                "Limites automáticos: desligado / uma vez a cada 5 minutos / uma vez a cada 2 minutos. Perguntas diretas têm prioridade."),
                color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
            TextButton(onClick = { open = !open }, modifier = Modifier.testTag("ai-context-filters")) {
                Text(tr("Chat context filters", "Filtros do contexto"))
            }
            if (open) {
                SettingSwitch(tr("Ignore !commands and /commands", "Ignorar !comandos e /comandos"), ai.filters.commands, { ai.updateFilters(ai.filters.copy(commands = it)) })
                SettingSwitch(tr("Limit repeated messages and author bursts", "Limitar repetições e rajadas por autor"), ai.filters.repetitions, { ai.updateFilters(ai.filters.copy(repetitions = it)) })
                OutlinedTextField(value = ai.filters.bots.joinToString(","), onValueChange = {
                    ai.updateFilters(ai.filters.copy(bots = it.take(2000).split(',').map(String::trim).toSet()))
                }, modifier = Modifier.fillMaxWidth().testTag("ai-ignored-bots"),
                    label = { Text(tr("Ignored bot names, comma separated", "Bots ignorados, separados por vírgula")) },
                    textStyle = MaterialTheme.typography.body2,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = RockyColors.Surface, unfocusedBorderColor = RockyColors.Border,
                        focusedBorderColor = RockyColors.Accent, textColor = RockyColors.TextPrimary,
                    ))
                Text(tr("Filters affect AI and the question queue; the displayed chat stays intact.",
                    "Os filtros afetam IA e fila de perguntas; o chat exibido permanece intacto."),
                    color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
            }
        }
    }
}
