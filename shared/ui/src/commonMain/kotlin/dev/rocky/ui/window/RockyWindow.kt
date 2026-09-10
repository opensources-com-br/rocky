package dev.rocky.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.rocky.core.live.LiveNote
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.core.ai.AiSuggestionClient
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.LiveIdea
import dev.rocky.core.live.LiveSessionMode
import dev.rocky.core.live.LiveSessionStatus
import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.notes.NoteRepository
import dev.rocky.core.twitch.TwitchChatClient
import dev.rocky.core.twitch.TwitchConnectionListener
import dev.rocky.core.twitch.TwitchConnectionPhase
import dev.rocky.core.voice.AudioInputDevice
import dev.rocky.core.voice.LocalTranscriptionConfiguration
import dev.rocky.core.voice.SystemVoice
import dev.rocky.core.voice.VoiceConfiguration
import dev.rocky.core.voice.VoiceOutputConfiguration
import dev.rocky.core.voice.VoiceService
import dev.rocky.ui.theme.RockyColors
import dev.rocky.ui.theme.RockyTheme
import kotlinx.coroutines.delay

@Composable
fun RockyWindow(
    compact: Boolean,
    pinned: Boolean,
    onTogglePinned: () -> Unit,
    onToggleCompact: () -> Unit,
    noteRepository: NoteRepository? = null,
    twitchChatClient: TwitchChatClient = InactiveTwitchChatClient,
    aiSuggestionClient: AiSuggestionClient = InactiveAiSuggestionClient,
    voiceService: VoiceService = InactiveVoiceService,
    initialAiConfiguration: AiProviderConfiguration = AiProviderConfiguration(
        AiProviderKind.Ollama,
        AiSuggestionState.DEFAULT_OLLAMA_ENDPOINT,
        AiSuggestionState.DEFAULT_OLLAMA_MODEL,
    ),
    onAiConfigurationChange: (AiProviderConfiguration) -> Unit = {},
    initialVoiceConfiguration: VoiceConfiguration = VoiceConfiguration(),
    onVoiceConfigurationChange: (VoiceConfiguration) -> Unit = {},
    onChooseWhisperExecutable: () -> String? = { null },
    onChooseWhisperModel: () -> String? = { null },
    initialTwitchClientId: String = "",
    onTwitchClientIdChange: (String) -> Unit = {},
    onOpenTwitchAuthorization: (String) -> Unit = {},
    onExportNotes: (List<LiveNote>) -> Boolean = { false },
    onExportIdeas: (List<LiveIdea>) -> Boolean = { false },
    onSettingsVisibilityChanged: (Boolean) -> Unit = {},
    initialMainSectionIndex: Int = 0,
    initialSettingsOpen: Boolean = false,
    initialSettingsSectionIndex: Int = 0,
) {
    RockyTheme {
        var settingsOpen by remember { mutableStateOf(initialSettingsOpen) }
        var mainSection by remember {
            mutableStateOf(MainSection.entries[initialMainSectionIndex])
        }
        var settingsSection by remember {
            mutableStateOf(SettingsSection.entries[initialSettingsSectionIndex])
        }
        var silenced by remember { mutableStateOf(false) }
        val live = rememberSimulatedLiveState()
        val twitch = remember(twitchChatClient) { TwitchLiveState(twitchChatClient) }
        val ai = remember(aiSuggestionClient) {
            AiSuggestionState(aiSuggestionClient, initialAiConfiguration, onAiConfigurationChange)
        }
        val voice = remember(voiceService) {
            VoiceState(voiceService, initialVoiceConfiguration, onVoiceConfigurationChange)
        }
        val aiScope = rememberCoroutineScope()
        var twitchClientId by remember { mutableStateOf(initialTwitchClientId) }
        val transientNotes = remember { TransientNoteRepository() }
        val resolvedNoteRepository = noteRepository ?: transientNotes
        val localNotes = remember(resolvedNoteRepository) { LocalNotesState(resolvedNoteRepository) }
        val sessionStatus = when {
            !twitch.isRealSession -> live.status
            twitch.phase == TwitchConnectionPhase.Connected -> LiveSessionStatus.Running
            twitch.phase == TwitchConnectionPhase.Failed -> LiveSessionStatus.Ended
            else -> LiveSessionStatus.Stopped
        }
        val visibleMessages = if (twitch.isRealSession) twitch.messages else live.messages
        val visibleSuggestion = if (twitch.isRealSession) ai.suggestion else live.suggestion
        val visiblePlatforms = if (twitch.isRealSession) twitch.platforms else samplePlatforms

        LaunchedEffect(twitch.phase, ai.automaticAnalysis) {
            while (twitch.phase == TwitchConnectionPhase.Connected && ai.automaticAnalysis) {
                delay(AUTOMATIC_ANALYSIS_INTERVAL_MILLIS)
                ai.analyze(aiScope, twitch.messages, automatic = true)
            }
        }

        LaunchedEffect(visibleSuggestion?.id, silenced) {
            visibleSuggestion?.let { voice.speakSuggestion(aiScope, it.id, it.text, silenced) }
        }

        LaunchedEffect(sessionStatus) {
            if (sessionStatus != LiveSessionStatus.Running) {
                voice.stopSpeaking()
                voice.cancelCapture()
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize().testTag("rocky-window"),
            color = RockyColors.Background,
        ) {
            Column {
                RockyHeader(
                    compact = compact,
                    pinned = pinned,
                    sessionStatus = sessionStatus,
                    twitchPhase = twitch.phase.takeIf { twitch.isRealSession },
                    onTogglePinned = onTogglePinned,
                    onToggleCompact = onToggleCompact,
                    onOpenSettings = {
                        settingsOpen = true
                        onSettingsVisibilityChanged(true)
                    },
                )
                Divider(color = RockyColors.Divider)
                when {
                    compact -> CompactContent(
                        status = sessionStatus,
                        messageCount = visibleMessages.size,
                        suggestion = visibleSuggestion?.text,
                        real = twitch.isRealSession,
                    )
                    settingsOpen -> {
                        SettingsHeading(
                            onDone = {
                                settingsOpen = false
                                onSettingsVisibilityChanged(false)
                            },
                        )
                        SettingsNavigation(settingsSection) { settingsSection = it }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            when (settingsSection) {
                                SettingsSection.Agent -> AgentSettings()
                                SettingsSection.Ai -> AiSettings(ai)
                                SettingsSection.Voice -> VoiceSettings(
                                    voice,
                                    onChooseWhisperExecutable,
                                    onChooseWhisperModel,
                                )
                                SettingsSection.Platforms -> PlatformSettings(
                                    clientId = twitchClientId,
                                    onClientIdChange = { value ->
                                        twitchClientId = value
                                        onTwitchClientIdChange(value)
                                    },
                                    twitch = twitch,
                                    onConnect = {
                                        live.end()
                                        ai.dismissSuggestion()
                                        twitch.connect(twitchClientId)
                                    },
                                    onDisconnect = twitch::disconnect,
                                    onOpenAuthorization = onOpenTwitchAuthorization,
                                )
                            }
                        }
                    }
                    else -> {
                        if (twitch.isRealSession) {
                            TwitchSessionControls(twitch, twitch::disconnect)
                        } else {
                            LiveSessionControls(
                                status = live.status,
                                onStart = {
                                    silenced = false
                                    live.start()
                                },
                                onEnd = {
                                    silenced = false
                                    live.end()
                                },
                                onRestart = {
                                    silenced = false
                                    live.restart()
                                },
                            )
                        }
                        Divider(color = RockyColors.Divider)
                        PlatformStrip(visiblePlatforms)
                        Divider(color = RockyColors.Divider)
                        LiveSummary(
                            suggestion = visibleSuggestion,
                            sourceCounts = if (twitch.isRealSession) {
                                mapOf(StreamPlatform.Twitch to (ai.suggestion?.sourceMessageIds?.size ?: 0))
                            } else {
                                live.sourceCounts
                            },
                            sessionStatus = sessionStatus,
                            sessionMode = if (twitch.isRealSession) LiveSessionMode.Real else LiveSessionMode.Demonstration,
                            suggestionSaved = live.suggestionSaved,
                            silenced = silenced,
                            generatingSuggestion = ai.generating,
                            canAnalyze = twitch.isRealSession && twitch.messages.isNotEmpty() && ai.isReady,
                            analysisStatus = ai.status,
                            onSaveNote = {
                                val note = if (twitch.isRealSession) {
                                    ai.suggestion?.let { suggestion ->
                                        LiveNote(suggestion.id, suggestion.text, "agora", "SUGESTÃO IA")
                                    }
                                } else {
                                    live.createNoteFromSuggestion()
                                }
                                if (note != null && localNotes.save(note)) {
                                    if (twitch.isRealSession) ai.dismissSuggestion() else live.markSuggestionSaved()
                                    mainSection = MainSection.Notes
                                }
                            },
                            onAnalyze = { ai.analyze(aiScope, twitch.messages) },
                            onNext = {
                                voice.stopSpeaking()
                                if (twitch.isRealSession) ai.dismissSuggestion() else live.dismissSuggestion()
                            },
                            onSilence = {
                                silenced = !silenced
                                if (silenced) voice.stopSpeaking()
                            },
                        )
                        MainNavigation(mainSection) { mainSection = it }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            when (mainSection) {
                                MainSection.Conversation -> ConversationContent(visibleMessages, voice.transcript)
                                MainSection.Support -> SupportContent()
                                MainSection.Notes -> NotesContent(
                                    notes = localNotes.notes,
                                    notice = localNotes.notice,
                                    onUpdate = localNotes::update,
                                    onDelete = localNotes::delete,
                                    onExport = {
                                        localNotes.setExportResult(onExportNotes(localNotes.notes))
                                    },
                                )
                                MainSection.Ideas -> TimelineContent(
                                    section = mainSection,
                                    onExportIdeas = onExportIdeas,
                                )
                                MainSection.Pulse -> PulseContent(visiblePlatforms)
                            }
                        }
                        Divider(color = RockyColors.Divider)
                        AssistantFooter(
                            active = voice.capturing,
                            busy = voice.transcribing,
                            status = voice.status,
                            onTalk = {
                                if (voice.capturing) {
                                    voice.stopCapture(aiScope) { request ->
                                        if (twitch.isRealSession && twitch.messages.isNotEmpty()) {
                                            ai.analyze(aiScope, twitch.messages, streamerRequest = request)
                                        }
                                    }
                                } else {
                                    voice.startCapture(aiScope) { request ->
                                        if (twitch.isRealSession && twitch.messages.isNotEmpty()) {
                                            ai.analyze(aiScope, twitch.messages, streamerRequest = request)
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

private object InactiveVoiceService : VoiceService {
    override fun availableVoices(): List<SystemVoice> = emptyList()
    override fun availableMicrophones(): List<AudioInputDevice> = emptyList()
    override fun speak(text: String, configuration: VoiceOutputConfiguration) = Unit
    override fun stopSpeaking() = Unit
    override fun startCapture(microphoneId: String?) = Unit
    override fun stopCaptureAndTranscribe(configuration: LocalTranscriptionConfiguration) = ""
    override fun cancelCapture() = Unit
    override fun close() = Unit
}

private object InactiveTwitchChatClient : TwitchChatClient {
    override fun connect(clientId: String, listener: TwitchConnectionListener) = Unit

    override fun disconnect() = Unit

    override fun close() = Unit
}

private object InactiveAiSuggestionClient : AiSuggestionClient {
    override fun testConnection(configuration: AiProviderConfiguration) =
        AiConnectionResult(false, "Provedor de IA indisponível")

    override fun generateSuggestion(
        configuration: AiProviderConfiguration,
        messages: List<ChatMessage>,
        streamerRequest: String?,
    ): AiGeneratedSuggestion? =
        null

    override fun close() = Unit
}

@Composable
private fun CompactContent(
    status: LiveSessionStatus,
    messageCount: Int,
    suggestion: String?,
    real: Boolean,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)) {
        Text(
            text = if (real) "Chat real da Twitch" else when (status) {
                LiveSessionStatus.Stopped -> "Demonstração parada"
                LiveSessionStatus.Running -> suggestion ?: "Ouvindo a demonstração"
                LiveSessionStatus.Ended -> "Demonstração encerrada"
            },
            style = MaterialTheme.typography.subtitle1,
            color = RockyColors.TextPrimary,
        )
        Text(
            text = if (real) "$messageCount mensagens recebidas" else "$messageCount mensagens simuladas · sem live real",
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.caption,
            color = RockyColors.TextSecondary,
        )
    }
}

private val TwitchLiveState.platforms: List<PlatformStatus>
    get() = listOf(
        PlatformStatus(
            name = "Twitch",
            account = account?.let { "@${it.login}" } ?: "Conectando",
            audience = "—",
            messagesPerMinute = 0,
            colorKey = PlatformColor.Twitch,
            enabled = phase != TwitchConnectionPhase.Failed,
        ),
        PlatformStatus("Kick", "Em breve", "0", 0, PlatformColor.Kick, enabled = false),
        PlatformStatus("YouTube", "Em breve", "0", 0, PlatformColor.YouTube, enabled = false),
        PlatformStatus("Facebook", "Em breve", "0", 0, PlatformColor.Offline, enabled = false),
    )

private const val AUTOMATIC_ANALYSIS_INTERVAL_MILLIS = 15_000L
