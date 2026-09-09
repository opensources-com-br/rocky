package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun VoiceSettings() {
    var voice by remember { mutableStateOf("Aurora") }
    var speed by remember { mutableStateOf(0.5f) }
    var volume by remember { mutableStateOf(0.7f) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        SettingTitle("Voz", "Timbre usado no seu fone.", voice)
        ChoiceRow(listOf("Aurora", "Íris", "Nuno", "Sem voz"), voice) { voice = it }
        Spacer(Modifier.height(16.dp))
        SettingTitle("Velocidade", "Ritmo da fala.", "${(speed * 100 + 50).toInt()}%")
        RockySlider(speed) { speed = it }
        Spacer(Modifier.height(10.dp))
        SettingTitle(
            "Volume no monitor",
            "Só você ouve; não entra na transmissão.",
            "${(volume * 100).toInt()}%",
        )
        RockySlider(volume) { volume = it }
    }
}
