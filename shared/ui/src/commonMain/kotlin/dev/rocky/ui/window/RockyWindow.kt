package dev.rocky.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import dev.rocky.core.locale.RockyLanguage
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.core.ai.AiSuggestionClient
import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.LiveIdea
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
    initialAgentConfiguration: AgentConfiguration = AgentConfiguration(),
    onAgentConfigurationChange: (AgentConfiguration) -> Unit = {},
    voiceService: VoiceService = InactiveVoiceService,
    initialAiConfiguration: AiProviderConfiguration = AiProviderConfiguration(
        AiProviderKind.Ollama,
        AiSuggestionState.DEFAULT_OLLAMA_ENDPOINT,
        AiSuggestionState.DEFAULT_OLLAMA_MODEL,
    ),
    onAiConfigurationChange: (AiProviderConfiguration) -> Unit = {},
    initialAutomaticAnalysis: Boolean = false,
    onAutomaticAnalysisChange: (Boolean) -> Unit = {},
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
    initialFirstUseOpen: Boolean = false,
    onFirstUseFinished: () -> Unit = {},
    initialLanguage: RockyLanguage = RockyLanguage.English,
    onLanguageChange: (RockyLanguage) -> Unit = {},
    currentTimeLabel: () -> String = { "agora" },
    currentTimeMillis: () -> Long = { 0L },
    initialMainSectionIndex: Int = 0,
    initialSettingsOpen: Boolean = false,
    initialSettingsSectionIndex: Int = 0,
) {
    RockyTheme {
        var settingsOpen by remember { mutableStateOf(initialSettingsOpen) }
        var firstUseOpen by remember { mutableStateOf(initialFirstUseOpen) }
        var language by remember { mutableStateOf(initialLanguage) }
        var mainSection by remember {
            mutableStateOf(MainSection.entries[initialMainSectionIndex])
        }
        var settingsSection by remember {
            mutableStateOf(SettingsSection.entries[initialSettingsSectionIndex])
        }
        var silenced by remember { mutableStateOf(false) }
        var waitingForVoiceCommand by remember { mutableStateOf(false) }
        val twitch = remember(twitchChatClient) { TwitchLiveState(twitchChatClient, currentTimeMillis) }
        val ai = remember(aiSuggestionClient) {
            AiSuggestionState(
                aiSuggestionClient, initialAiConfiguration,
                initialAutomaticAnalysis, onAutomaticAnalysisChange, onAiConfigurationChange,
            )
        }
        val agent = remember { AgentState(initialAgentConfiguration, onAgentConfigurationChange) }
        val voice = remember(voiceService) {
            VoiceState(
                voiceService,
                initialVoiceConfiguration,
                onConfigurationChange = onVoiceConfigurationChange,
            )
        }
        val aiScope = rememberCoroutineScope()
        val mainContentScrollState = rememberScrollState()
        var twitchClientId by remember { mutableStateOf(initialTwitchClientId) }
        val transientNotes = remember { TransientNoteRepository() }
        val resolvedNoteRepository = noteRepository ?: transientNotes
        val localNotes = remember(resolvedNoteRepository) { LocalNotesState(resolvedNoteRepository) }
        val sessionStatus = when {
            twitch.phase == TwitchConnectionPhase.Connected -> LiveSessionStatus.Running
            twitch.phase == TwitchConnectionPhase.Failed -> LiveSessionStatus.Ended
            else -> LiveSessionStatus.Stopped
        }
        val visibleMessages = twitch.messages
        val visibleMessageCount = twitch.totalMessages
        val visibleSuggestion = ai.suggestion
        val visiblePlatforms = twitch.platforms

        LaunchedEffect(
            twitch.phase,
            twitch.totalMessages,
            ai.automaticAnalysis,
            ai.analysisRevision,
            agent.configuration.interventionsPerTenMinutes,
        ) {
            if (twitch.phase == TwitchConnectionPhase.Connected && ai.automaticAnalysis) {
                delay(ai.automaticAnalysisDelay(currentTimeMillis(), agent.configuration.analysisIntervalMillis))
                ai.analyze(
                    aiScope,
                    twitch.messagesReceivedWithin(VOICE_CHAT_WINDOW_MILLIS),
                    automatic = true,
                    agent = agent.configuration,
                    automaticTimeMillis = currentTimeMillis(),
                )
            }
        }

        LaunchedEffect(twitch.phase) {
            while (twitch.isRealSession) {
                delay(METRICS_REFRESH_MILLIS)
                twitch.refreshMetrics()
            }
        }

        LaunchedEffect(visibleSuggestion?.id, silenced) {
            visibleSuggestion?.let { voice.speakSuggestion(aiScope, it.id, it.text, silenced) }
        }

        LaunchedEffect(sessionStatus) {
            if (sessionStatus != LiveSessionStatus.Running) {
                waitingForVoiceCommand = false
                voice.resetSession()
                if (twitch.isRealSession && ai.generating) ai.cancelAnalysis()
            }
        }

        val analyzeVoiceCommand: (String) -> Unit = { request ->
            val recentMessages = twitch.messagesReceivedWithin(VOICE_CHAT_WINDOW_MILLIS)
            if (recentMessages.isEmpty()) {
                voice.speakAcknowledgement(
                    aiScope,
                    "Não encontrei mensagens nos últimos dois minutos.",
                    silenced,
                    voice::resumeListener,
                )
            } else ai.analyze(
                scope = aiScope,
                messages = recentMessages,
                streamerRequest = request,
                agent = agent.configuration,
                messageLimit = recentMessages.size,
                onComplete = { suggestion ->
                    if (voice.listenerEnabled) {
                        if (suggestion == null) {
                            voice.speakAcknowledgement(
                                aiScope,
                                "Não consegui consultar o chat agora. Vou continuar ouvindo.",
                                silenced,
                                voice::resumeListener,
                            )
                        } else {
                            voice.speakSuggestion(
                                aiScope,
                                suggestion.id,
                                suggestion.text,
                                silenced,
                                force = true,
                                onFinished = voice::resumeListener,
                            )
                        }
                    }
                },
            )
        }
        val submitVoiceCommand: (String) -> Unit = { command ->
            waitingForVoiceCommand = false
            voice.speakAcknowledgement(aiScope, "Vou verificar o chat.", silenced) {
                if (voice.listenerEnabled) analyzeVoiceCommand(command)
            }
        }
        val handleVoiceRequest: (String) -> Unit = { transcript ->
            val directCommand = extractRockyCommand(transcript)
            when {
                directCommand != null -> submitVoiceCommand(directCommand)
                waitingForVoiceCommand -> submitVoiceCommand(transcript.trim())
                containsRockyWakeWord(transcript) -> {
                    waitingForVoiceCommand = true
                    voice.speakAcknowledgement(aiScope, "Estou ouvindo.", silenced, voice::resumeListener)
                }
                else -> voice.resumeListener()
            }
        }

        LaunchedEffect(twitch.phase, voice.transcriptionReady) {
            if (twitch.phase == TwitchConnectionPhase.Connected && voice.transcriptionReady) {
                voice.enableListener(aiScope, handleVoiceRequest)
            }
        }

        CompositionLocalProvider(LocalRockyLanguage provides language) {
        Surface(
            modifier = Modifier.fillMaxSize().testTag("rocky-window"),
            color = RockyColors.Background,
        ) {
            Column {
                RockyHeader(
                    agentName = agent.displayName,
                    compact = compact,
                    pinned = pinned,
                    sessionStatus = sessionStatus,
                    twitchPhase = twitch.phase.takeIf { twitch.isRealSession },
                    microphoneActive = voice.capturing,
                    onTogglePinned = onTogglePinned,
                    onToggleCompact = onToggleCompact,
                    onOpenSettings = {
                        settingsOpen = true
                        onSettingsVisibilityChanged(true)
                    },
                )
                Divider(color = RockyColors.Divider)
                when {
                    compact && !settingsOpen -> CompactContent(
                        status = sessionStatus,
                        messageCount = visibleMessageCount,
                        suggestion = visibleSuggestion?.text,
                        silenced = silenced,
                        onToggleSilence = {
                            silenced = !silenced
                            if (silenced) voice.interruptSpeech()
                        },
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
                                SettingsSection.Agent -> AgentSettings(
                                    agent = agent,
                                    language = language,
                                    onLanguageChange = {
                                        language = it
                                        onLanguageChange(it)
                                    },
                                )
                                SettingsSection.Ai -> AiSettings(ai)
                                SettingsSection.Voice -> VoiceSettings(
                                    voice,
                                    agent.displayName,
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
                                        silenced = false
                                        voice.resetSession()
                                        ai.resetSession()
                                        twitch.connect(twitchClientId)
                                    },
                                    onDisconnect = {
                                        silenced = false
                                        voice.resetSession()
                                        ai.resetSession()
                                        twitch.disconnect()
                                    },
                                    onOpenBrowser = onOpenTwitchAuthorization,
                                )
                            }
                        }
                    }
                    firstUseOpen -> {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            FirstUseContent(
                                twitchConnected = twitch.phase == TwitchConnectionPhase.Connected,
                                aiVerified = ai.connectionVerified,
                                voiceVerified = voice.voiceTested && voice.transcriptionReady,
                                onConfigureTwitch = {
                                    settingsSection = SettingsSection.Platforms
                                    settingsOpen = true
                                    onSettingsVisibilityChanged(true)
                                },
                                onConfigureAi = {
                                    settingsSection = SettingsSection.Ai
                                    settingsOpen = true
                                    onSettingsVisibilityChanged(true)
                                },
                                onConfigureVoice = {
                                    settingsSection = SettingsSection.Voice
                                    settingsOpen = true
                                    onSettingsVisibilityChanged(true)
                                },
                                onComplete = {
                                    firstUseOpen = false
                                    onFirstUseFinished()
                                },
                            )
                        }
                    }
                    else -> {
                        if (twitch.isRealSession) {
                            TwitchSessionControls(twitch) {
                                silenced = false
                                voice.resetSession()
                                ai.resetSession()
                                twitch.disconnect()
                            }
                        }
                        Divider(color = RockyColors.Divider)
                        PlatformStrip(visiblePlatforms)
                        Divider(color = RockyColors.Divider)
                        LiveSummary(
                            agentName = agent.displayName,
                            suggestion = visibleSuggestion,
                            sourceCounts = mapOf(StreamPlatform.Twitch to (ai.suggestion?.sourceMessageIds?.size ?: 0)),
                            sessionStatus = sessionStatus,
                            sessionAvailable = twitch.isRealSession,
                            suggestionSaved = false,
                            silenced = silenced,
                            speaking = voice.speaking,
                            generatingSuggestion = ai.generating,
                            canAnalyze = twitch.phase == TwitchConnectionPhase.Connected && twitch.messages.isNotEmpty() && ai.isReady,
                            analysisStatus = ai.status,
                            evidence = ai.suggestionSources.map(::messageEvidence),
                            onSaveNote = {
                                val note = ai.suggestion?.let { suggestion ->
                                    suggestionNote(suggestion, ai.suggestionSources, currentTimeLabel())
                                }
                                if (note != null && localNotes.save(note)) {
                                    ai.dismissSuggestion()
                                    mainSection = MainSection.Notes
                                }
                            },
                            onAnalyze = { ai.analyze(aiScope, twitch.messagesReceivedWithin(VOICE_CHAT_WINDOW_MILLIS), agent = agent.configuration) },
                            onNext = {
                                voice.interruptSpeech()
                                ai.dismissSuggestion()
                            },
                            onSilence = {
                                silenced = !silenced
                                if (silenced) voice.interruptSpeech()
                            },
                        )
                        MainNavigation(mainSection) { mainSection = it }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (mainSection == MainSection.Conversation) Modifier
                                    else Modifier.verticalScroll(mainContentScrollState),
                                ),
                        ) {
                            when (mainSection) {
                                MainSection.Conversation -> ConversationContent(
                                    messages = visibleMessages,
                                    streamerSpeech = voice.transcript,
                                    showTextRequest = true,
                                    textRequestEnabled = twitch.phase == TwitchConnectionPhase.Connected && ai.isReady && !ai.generating,
                                    analyzing = ai.generating,
                                    hasCaptureGaps = twitch.hasCaptureGaps,
                                    analysisStatus = ai.status,
                                    performanceNotice = ai.lastDurationMillis?.let {
                                        "${it} ms · ${ai.completedRequests} análises · ${ai.reportedTokens} tokens informados (parcial)"
                                    },
                                    onCancelAnalysis = { ai.cancelAnalysis(); voice.resumeListener() },
                                    onTextRequest = { request ->
                                        ai.analyze(aiScope, twitch.messagesReceivedWithin(VOICE_CHAT_WINDOW_MILLIS), streamerRequest = request, agent = agent.configuration)
                                    },
                                )
                                MainSection.Support -> SupportContent()
                                MainSection.Notes -> NotesContent(
                                    notes = localNotes.notes,
                                    loadFailed = localNotes.loadFailed,
                                    onReload = localNotes::reload,
                                    notice = localNotes.notice,
                                    onUpdate = localNotes::update,
                                    onDelete = localNotes::delete,
                                    onExport = {
                                        localNotes.export(onExportNotes)
                                    },
                                )
                                MainSection.Ideas -> TimelineContent(
                                    onExportIdeas = onExportIdeas,
                                )
                                MainSection.Pulse -> PulseContent(
                                    platforms = visiblePlatforms,
                                )
                            }
                        }
                        Divider(color = RockyColors.Divider)
                        AssistantFooter(
                            agentName = agent.displayName,
                            active = voice.listenerEnabled,
                            capturing = voice.capturing,
                            inputLevel = voice.inputLevel,
                            busy = voice.transcribing,
                            status = voice.status,
                            viewerCount = twitch.viewerCount,
                            messagesPerMinute = twitch.messagesPerMinute,
                            onTalk = { voice.toggleListener(aiScope, handleVoiceRequest) },
                        )
                    }
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
        agent: AgentConfiguration,
    ): AiGeneratedSuggestion? =
        null

    override fun close() = Unit
}

@Composable
internal fun CompactContent(
    status: LiveSessionStatus,
    messageCount: Int,
    suggestion: String?,
    silenced: Boolean,
    onToggleSilence: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 4.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text(
                text = "$messageCount mensagens recebidas",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.caption,
            )
            androidx.compose.material.TextButton(onClick = onToggleSilence) {
                Text(if (silenced) "Retomar" else "Silenciar")
            }
        }
        Text(
            text = compactHeadline(status, suggestion),
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            style = MaterialTheme.typography.subtitle1,
            color = RockyColors.TextPrimary,
        )
    }
}

internal fun compactHeadline(status: LiveSessionStatus, suggestion: String?): String = suggestion ?: when (status) {
    LiveSessionStatus.Stopped -> "Conecte sua Twitch"
    LiveSessionStatus.Running -> "Chat real da Twitch"
    LiveSessionStatus.Ended -> "Conexão da Twitch encerrada"
}

private val TwitchLiveState.platforms: List<PlatformStatus>
    get() = listOf(
        PlatformStatus(
            name = "Twitch",
            account = account?.let { "@${it.login}" } ?: "Conectando",
            audience = viewerCount?.toString() ?: "—",
            messagesPerMinute = messagesPerMinute,
            colorKey = PlatformColor.Twitch,
            enabled = phase != TwitchConnectionPhase.Failed,
        ),
        PlatformStatus("Kick", "Em breve", "0", 0, PlatformColor.Offline, enabled = false),
        PlatformStatus("YouTube", "Em breve", "0", 0, PlatformColor.Offline, enabled = false),
        PlatformStatus("Facebook", "Em breve", "0", 0, PlatformColor.Offline, enabled = false),
    )

private val AgentConfiguration.analysisIntervalMillis: Long
    get() = 600_000L / interventionsPerTenMinutes.coerceIn(1, 9)

private const val METRICS_REFRESH_MILLIS = 5_000L
private const val VOICE_CHAT_WINDOW_MILLIS = 120_000L
