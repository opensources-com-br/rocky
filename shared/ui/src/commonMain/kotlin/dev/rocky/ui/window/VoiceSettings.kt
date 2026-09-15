package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.core.voice.SpeechProvider
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
    var pathsOpen by remember { mutableStateOf(false) }
    var timingsOpen by remember { mutableStateOf(false) }
    LaunchedEffect(voice) { voice.loadDevices(scope) }
    val cloudVoice = voice.configuration.output.provider == SpeechProvider.ElevenLabs

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        VoiceProviderSettings(voice)
        SettingsPreferenceGroup(tr("Playback", "Reprodução")) {
            VoiceToggleRow(tr("Read new suggestions", "Ler novas sugestões"),
                voice.configuration.readSuggestions, "voice-read-suggestions", voice::updateReadSuggestions)
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            if (cloudVoice) Text(tr("System voice for optional fallback", "Voz do sistema para alternativa local"),
                modifier = Modifier.padding(start = 16.dp, top = 12.dp), color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption)
            VoiceSelector(voice.voices, voice.configuration.output.voiceId, voice.loadingDevices, voice::updateVoice)
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val minSpeed = if (cloudVoice) 70 else 50
                val speedRange = if (cloudVoice) 50 else 100
                VoiceSliderSetting(tr("Speed", "Velocidade"), tr("Speech pace.", "Ritmo da fala."),
                    "${voice.configuration.output.speedPercent}%") {
                    RockySlider(((voice.configuration.output.speedPercent - minSpeed) / speedRange.toFloat()).coerceIn(0f, 1f)) {
                        voice.updateSpeed((it * speedRange).toInt() + minSpeed)
                    }
                }
                VoiceSliderSetting(tr("Volume", "Volume"),
                    tr("Available with ElevenLabs and local Windows voices. For local macOS voices, use the system volume.",
                        "Disponível na ElevenLabs e na voz local do Windows. Para a voz local do macOS, ajuste o volume do sistema."),
                    "${voice.configuration.output.volumePercent}%") {
                    RockySlider(voice.configuration.output.volumePercent / 100f, enabled = voice.outputVolumeSupported) {
                        voice.updateVolume((it * 100).toInt())
                    }
                }
                Button(
                    modifier = Modifier.testTag("test-voice"),
                    onClick = { if (voice.speaking) voice.interruptSpeech() else voice.testVoice(scope, agentName) },
                    colors = voiceActionColors(), shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp),
                ) { Text(if (voice.speaking) tr("Stop voice", "Parar voz") else tr("Test voice", "Testar voz")) }
            }
        }
        SettingsPreferenceGroup(tr("Microphone input", "Entrada do microfone")) {
            MicrophoneSelector(voice)
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tr("Speech recognition", "Reconhecimento de voz"), modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.body2)
                    Text(if (voice.transcriptionReady) tr("Ready", "Pronto") else tr("Not configured", "Não configurado"),
                        color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
                }
                Text(tr("The microphone reacts when you call “$agentName”. Speech ends after silence; long phrases can last up to 30 seconds. Capture pauses during replies.",
                    "O microfone reage quando você chama “$agentName”. O silêncio encerra a frase; falas longas podem durar até 30 segundos. A captura pausa nas respostas."),
                    color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
                if (!voice.transcriptionReady && voice.automaticTranscriptionSetupSupported) {
                    Text(tr("Set up local recognition once. Rocky installs the engine and downloads the voice model.",
                        "Prepare o reconhecimento local uma vez. O Rocky instalará o mecanismo e baixará o modelo de voz."),
                        color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
                    Button(
                        modifier = Modifier.testTag("prepare-transcription"), enabled = !voice.preparingTranscription,
                        onClick = { voice.prepareTranscription(scope) }, colors = voiceActionColors(),
                        shape = RoundedCornerShape(8.dp), elevation = ButtonDefaults.elevation(0.dp, 0.dp),
                    ) { Text(if (voice.preparingTranscription) tr("Preparing…", "Preparando…") else tr("Set up speech recognition", "Configurar reconhecimento de voz")) }
                }
                if (voice.preparingTranscription) {
                    TextButton(onClick = voice::cancelTranscriptionSetup) { Text(tr("Cancel setup", "Cancelar preparação")) }
                }
                voice.transcriptionSetupStatus?.let {
                    Text(it, modifier = Modifier.testTag("transcription-setup-status"), color = RockyColors.TextSecondary,
                        style = MaterialTheme.typography.caption)
                }
                TextButton(onClick = { pathsOpen = !pathsOpen }, modifier = Modifier.testTag("voice-advanced-paths")) {
                    Text(tr("Advanced recognition settings", "Configuração avançada do reconhecimento"))
                }
                if (pathsOpen) {
                    VoicePathField(tr("whisper-cli executable", "Executável whisper-cli"),
                        voice.configuration.transcription.executablePath, "whisper-executable", voice::updateWhisperExecutable,
                        { onChooseWhisperExecutable()?.let(voice::updateWhisperExecutable) })
                    VoicePathField(tr("GGML model (.bin)", "Modelo GGML (.bin)"),
                        voice.configuration.transcription.modelPath, "whisper-model", voice::updateWhisperModel,
                        { onChooseWhisperModel()?.let(voice::updateWhisperModel) })
                }
            }
        }
        SettingsPreferenceGroup(tr("Speech detection", "Detecção de fala")) {
            VoiceToggleRow(tr("Detect end of speech", "Detectar fim da fala"),
                voice.configuration.detectEndOfSpeech, "voice-end-detection", voice::updateEndDetection)
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (voice.configuration.detectEndOfSpeech) {
                    VoiceSliderSetting(tr("Trailing silence", "Silêncio após a fala"),
                        tr("Silence before ending a phrase.", "Tempo de silêncio antes de encerrar a frase."),
                        "${voice.configuration.silenceMillis} ms") {
                        RockySlider((voice.configuration.silenceMillis - 450) / 1050f) {
                            voice.updateSilence(450 + (it * 1050).toLong())
                        }
                    }
                    VoiceSliderSetting(tr("Noise threshold", "Limiar de ruído"),
                        tr("Raise in noisy rooms; lower if quiet speech is missed.",
                            "Aumente em ambientes ruidosos; diminua se falas baixas forem ignoradas.")) {
                        RockySlider((voice.configuration.speechThreshold - 0.01f) / 0.14f) {
                            voice.updateSpeechThreshold(0.01f + it * 0.14f)
                        }
                    }
                }
                OutlinedButton(enabled = !voice.calibrating, modifier = Modifier.testTag("voice-calibrate"),
                    onClick = { voice.calibrate(scope) }) {
                    Text(if (voice.calibrating) tr("Calibrating… stay quiet", "Calibrando… fique em silêncio")
                        else tr("Calibrate microphone noise", "Calibrar ruído do microfone"))
                }
            }
        }
        SettingsPreferenceGroup(tr("Conversation test", "Teste de conversa")) {
            Column(Modifier.padding(16.dp).testTag("voice-conversation-test")) {
                Text(
                    if (voice.conversationTesting) {
                        "Fale agora. A gravação termina automaticamente."
                    } else {
                        "Fale uma frase; $agentName transcreve e responde em áudio."
                    },
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.caption,
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
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        if (voice.conversationTesting) "Ouvindo…" else "Testar conversa por voz",
                        fontWeight = FontWeight.Bold,
                    )
                }

            }
        }
        SettingsPreferenceGroup(tr("Diagnostics", "Diagnóstico")) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                TextButton(onClick = { timingsOpen = !timingsOpen }, modifier = Modifier.testTag("voice-audio-timings")) {
                    Text(tr("Last audio timing (local)", "Tempos do último áudio (locais)"))
                }
                if (timingsOpen) {
                    val timing = voice.telemetry
                    Text(tr("Capture", "Captura") + ": ${timing.captureMillis} ms · " +
                        tr("Transcription", "Transcrição") + ": ${timing.transcriptionMillis} ms",
                        color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
                    Text(tr("First cloud audio", "Primeiro áudio remoto") + ": ${timing.firstAudioMillis} ms · " +
                        tr("Speech operation", "Operação de fala") + ": ${timing.playbackMillis} ms",
                        modifier = Modifier.padding(top = 6.dp, bottom = 8.dp), color = RockyColors.TextSecondary,
                        style = MaterialTheme.typography.caption)
                }
            }
        }
        voice.status?.let { Text(it, color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption) }
        Text(
            tr("Monitor output may reach your stream if OBS captures all computer audio.",
                "A saída de monitoramento pode entrar na transmissão se o OBS capturar todo o áudio do computador."),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
private fun VoiceToggleRow(title: String, checked: Boolean, tag: String, onChange: (Boolean) -> Unit) {
    SettingsPreferenceRow(title) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Switch(checked = checked, onCheckedChange = onChange, modifier = Modifier.testTag(tag),
                colors = SwitchDefaults.colors(checkedThumbColor = RockyColors.TextPrimary,
                    checkedTrackColor = RockyColors.Accent, uncheckedThumbColor = RockyColors.TextPrimary,
                    uncheckedTrackColor = RockyColors.TextMuted))
        }
    }
}

@Composable
private fun VoiceSliderSetting(title: String, description: String, value: String? = null, slider: @Composable () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.body2)
            value?.let { Text(it, color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption) }
        }
        Text(description, modifier = Modifier.padding(top = 4.dp), color = RockyColors.TextSecondary,
            style = MaterialTheme.typography.caption)
        slider()
    }
}

@Composable
private fun VoiceSelector(voices: List<SystemVoice>, selectedId: String?, loading: Boolean, onSelect: (String?) -> Unit) {
    val defaultLabel = if (loading) tr("Loading…", "Carregando…") else tr("Default voice", "Voz padrão")
    val options = listOf("" to defaultLabel) + voices.map {
        it.id to "${it.name}${it.language?.let { language -> " · $language" }.orEmpty()}"
    }
    SpeechChoice(tr("System voice", "Voz do sistema"), selectedId ?: "", options, "voice-system-menu") {
        onSelect(it.takeIf(String::isNotEmpty))
    }
}

@Composable
private fun MicrophoneSelector(voice: VoiceState) {
    val defaultLabel = if (voice.loadingDevices) tr("Loading…", "Carregando…") else tr("Default input", "Entrada padrão")
    val options = listOf("" to defaultLabel) + voice.microphones.map { it.id to it.name }
    SpeechChoice(tr("Microphone", "Microfone"), voice.configuration.transcription.microphoneId ?: "",
        options, "voice-microphone-menu") { voice.updateMicrophone(it.takeIf(String::isNotEmpty)) }
}

@Composable
private fun voiceActionColors() = ButtonDefaults.buttonColors(backgroundColor = RockyColors.Accent, contentColor = Color.Black)

@Composable
private fun VoicePathField(label: String, value: String, tag: String, onValueChange: (String) -> Unit, onBrowse: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            modifier = Modifier.weight(1f).testTag(tag), value = value, onValueChange = onValueChange,
            label = { Text(label) }, singleLine = true, textStyle = MaterialTheme.typography.body2,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = RockyColors.Surface,
                textColor = RockyColors.TextPrimary, unfocusedBorderColor = RockyColors.Border, focusedBorderColor = RockyColors.Accent),
        )
        Spacer(Modifier.width(8.dp))
        OutlinedButton(onClick = onBrowse) { Text(tr("Choose", "Escolher")) }
    }
}
