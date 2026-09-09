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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun AgentSettings() {
    var agentName by remember { mutableStateOf("Rocky") }
    var tone by remember { mutableStateOf("Direto") }
    var frequency by remember { mutableStateOf(0.38f) }
    var canInterrupt by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp)) {
        SettingTitle("Nome do agente", "Como o chat e você chamam o assistente.")
        OutlinedTextField(
            value = agentName,
            onValueChange = { agentName = it },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
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
        Spacer(Modifier.height(20.dp))
        SettingTitle("Tom de voz", "Define como o agente formula as intervenções.", tone)
        ChoiceRow(listOf("Direto", "Animado", "Analítico", "Irônico"), tone) { tone = it }
        Spacer(Modifier.height(20.dp))
        SettingTitle(
            "Frequência de fala",
            "Quantas vezes por 10 minutos ele pode intervir.",
            "${(frequency * 8).toInt() + 1}×",
        )
        RockySlider(frequency) { frequency = it }
        Spacer(Modifier.height(12.dp))
        SettingTitle(
            "Interromper enquanto você fala",
            "Se desligado, ele espera uma pausa de 3 segundos.",
            if (canInterrupt) "ativo" else "inativo",
        )
        Spacer(Modifier.height(8.dp))
        SettingSwitch("pode interromper", canInterrupt) { canInterrupt = it }
    }
}
