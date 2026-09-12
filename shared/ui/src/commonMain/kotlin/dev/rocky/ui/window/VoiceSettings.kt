package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.core.voice.AudioInputDevice
import dev.rocky.core.voice.SystemVoice
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun VoiceSettings(
    voice: VoiceState,
    agentName: String = "Rocky",
    onChooseWhisperExecutable: () -> String? = { null },
    onChooseWhisperModel: () -> String? = { null },
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(voice) { voice.loadDevices(scope) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        SettingSwitch(tr("Detect end of speech", "Detectar fim da fala"), voice.configuration.detectEndOfSpeech, voice::updateEndDetection)
        if (voice.configuration.detectEndOfSpeech) {
            SettingTitle(tr("Trailing silence", "Silêncio após a fala"), "${voice.configuration.silenceMillis} ms")
            RockySlider((voice.configuration.silenceMillis - 450) / 1050f) { voice.updateSilence(450 + (it * 1050).toLong()) }
            SettingTitle(tr("Noise threshold", "Limiar de ruído"), tr("Raise in noisy rooms; lower if quiet speech is missed.", "Aumente em ambientes ruidosos; diminua se falas baixas forem ignoradas."))
            RockySlider((voice.configuration.speechThreshold - 0.01f) / 0.14f) { voice.updateSpeechThreshold(0.01f + it * 0.14f) }
        }
        SettingTitle(
            "Leitura das sugestões",
            "Usa uma voz instalada no sistema e a saída de áudio padrão.",
            if (voice.speaking) "falando" else null,
        )
        SettingSwitch("ler novas sugestões", voice.configuration.readSuggestions, voice::updateReadSuggestions)
        Spacer(Modifier.height(10.dp))
        VoiceSelector(voice.voices, voice.configuration.output.voiceId, voice.loadingDevices, voice::updateVoice)
        Spacer(Modifier.height(12.dp))
        SettingTitle("Velocidade", "Ritmo da fala.", "${voice.configuration.output.speedPercent}%")
        RockySlider((voice.configuration.output.speedPercent - 50) / 100f) {
            voice.updateSpeed((it * 100).toInt() + 50)
        }
        SettingTitle(
            "Volume",
            "Aplicado pelo Windows; no macOS, ajuste a saída padrão do sistema.",
            "${voice.configuration.output.volumePercent}%",
        )
        RockySlider(voice.configuration.output.volumePercent / 100f, enabled = voice.outputVolumeSupported) {
            voice.updateVolume((it * 100).toInt())
        }
        Button(
            modifier = Modifier.fillMaxWidth().height(40.dp).testTag("test-voice"),
            onClick = { if (voice.speaking) voice.interruptSpeech() else voice.testVoice(scope, agentName) },
            colors = ButtonDefaults.buttonColors(backgroundColor = RockyColors.Accent, contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(if (voice.speaking) "Parar voz" else "Testar voz", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(22.dp))
        SettingTitle(
            "Entrada do streamer",
            "O microfone fica ativo e reage quando você chama “$agentName”.",
            if (voice.transcriptionReady) "pronta" else "configurar",
        )
        if (!voice.transcriptionReady && voice.automaticTranscriptionSetupSupported) {
            Text(
                "Prepare o reconhecimento local uma vez. O Rocky instalará o mecanismo e baixará o modelo de voz.",
                modifier = Modifier.padding(top = 10.dp),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
            )
            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(40.dp)
                    .testTag("prepare-transcription"),
                enabled = !voice.preparingTranscription,
                onClick = { voice.prepareTranscription(scope) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = RockyColors.Accent,
                    contentColor = Color.Black,
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    if (voice.preparingTranscription) "Preparando…" else "Configurar reconhecimento de voz",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        if (voice.preparingTranscription) {
            Button(onClick = voice::cancelTranscriptionSetup) { Text("Cancelar preparação") }
        }
        Text(tr("Listen while the microphone is capturing. Each recording lasts eight seconds; capture pauses during transcription and replies.",
            "Fale enquanto o microfone estiver capturando. Cada gravação dura oito segundos; a captura pausa na transcrição e nas respostas."),
            style = MaterialTheme.typography.caption, color = RockyColors.TextMuted)
        voice.transcriptionSetupStatus?.let {
            Text(
                it,
                modifier = Modifier.padding(top = 6.dp).testTag("transcription-setup-status"),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
            )
        }
        Spacer(Modifier.height(10.dp))
        MicrophoneSelector(voice)
        Spacer(Modifier.height(10.dp))
        VoicePathField(
            label = "Executável whisper-cli",
            value = voice.configuration.transcription.executablePath,
            tag = "whisper-executable",
            onValueChange = voice::updateWhisperExecutable,
            onBrowse = { onChooseWhisperExecutable()?.let(voice::updateWhisperExecutable) },
        )
        Spacer(Modifier.height(8.dp))
        VoicePathField(
            label = "Modelo GGML (.bin)",
            value = voice.configuration.transcription.modelPath,
            tag = "whisper-model",
            onValueChange = voice::updateWhisperModel,
            onBrowse = { onChooseWhisperModel()?.let(voice::updateWhisperModel) },
        )
        Spacer(Modifier.height(14.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().testTag("voice-conversation-test"),
            color = RockyColors.SurfaceElevated,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, RockyColors.Border),
        ) {
            Column(Modifier.padding(12.dp)) {
                SettingTitle(
                    "Teste de conversa",
                    if (voice.conversationTesting) {
                        "Fale agora. A gravação termina automaticamente."
                    } else {
                        "Fale uma frase; $agentName transcreve e responde em áudio."
                    },
                    if (voice.capturing) "ouvindo" else null,
                )
                voice.conversationTestTranscript?.let {
                    Text(
                        "Você: $it",
                        modifier = Modifier.padding(top = 8.dp).testTag("voice-test-transcript"),
                        color = RockyColors.TextPrimary,
                        style = MaterialTheme.typography.body2,
                    )
                }
                voice.conversationTestResponse?.let {
                    Text(
                        "$agentName: $it",
                        modifier = Modifier.padding(top = 6.dp).testTag("voice-test-response"),
                        color = RockyColors.Accent,
                        style = MaterialTheme.typography.body2,
                    )
                }
                Button(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(40.dp)
                        .testTag("test-conversation"),
                    enabled = voice.transcriptionReady && !voice.conversationTesting,
                    onClick = { voice.testConversation(scope, agentName) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = RockyColors.Accent,
                        contentColor = Color.Black,
                    ),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        if (voice.conversationTesting) "Ouvindo…" else "Testar conversa por voz",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        voice.status?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 10.dp),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
            )
        }
        Text(
            text = "A saída de monitoramento pode entrar na transmissão se o OBS capturar todo o áudio do computador.",
            modifier = Modifier.padding(top = 14.dp, bottom = 12.dp),
            color = RockyColors.TextMuted,
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
private fun VoiceSelector(
    voices: List<SystemVoice>,
    selectedId: String?,
    loading: Boolean,
    onSelect: (String?) -> Unit,
) {
    val options = listOf<SystemVoice?>(null) + voices
    SelectionField(
        label = "Voz do sistema",
        value = when {
            loading -> "Carregando…"
            selectedId == null -> "Voz padrão"
            else -> voices.firstOrNull { it.id == selectedId }?.name ?: selectedId
        },
        options = options,
        optionLabel = { it?.let { item -> "${item.name}${item.language?.let { language -> " · $language" }.orEmpty()}" } ?: "Voz padrão" },
        onSelect = { onSelect(it?.id) },
    )
}

@Composable
private fun MicrophoneSelector(voice: VoiceState) {
    val options = listOf<AudioInputDevice?>(null) + voice.microphones
    val selectedId = voice.configuration.transcription.microphoneId
    SelectionField(
        label = "Microfone",
        value = selectedId?.let { id -> voice.microphones.firstOrNull { it.id == id }?.name ?: id }
            ?: if (voice.loadingDevices) "Carregando…" else "Entrada padrão",
        options = options,
        optionLabel = { it?.name ?: "Entrada padrão" },
        onSelect = { voice.updateMicrophone(it?.id) },
    )
}

@Composable
private fun <T> SelectionField(
    label: String,
    value: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            color = RockyColors.SurfaceElevated,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, RockyColors.Border),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, color = RockyColors.TextMuted, style = MaterialTheme.typography.caption)
                    Text(value, color = RockyColors.TextPrimary, style = MaterialTheme.typography.body2)
                }
                Text("⌄", color = RockyColors.TextSecondary)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onSelect(option)
                }) {
                    Text(optionLabel(option))
                }
            }
        }
    }
}

@Composable
private fun VoicePathField(
    label: String,
    value: String,
    tag: String,
    onValueChange: (String) -> Unit,
    onBrowse: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            modifier = Modifier.weight(1f).testTag(tag),
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
        )
        Spacer(Modifier.width(7.dp))
        Button(
            onClick = onBrowse,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = RockyColors.SurfaceElevated,
                contentColor = RockyColors.TextPrimary,
            ),
            shape = RoundedCornerShape(9.dp),
        ) {
            Text("Escolher")
        }
    }
}
