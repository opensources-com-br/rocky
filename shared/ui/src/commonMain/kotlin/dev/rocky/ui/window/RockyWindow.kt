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
import dev.rocky.core.kick.KickChatClient
import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.kick.KickConnectionListener
import dev.rocky.core.kick.KickConnectionPhase
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
    shortcutKeys: List<Int> = listOf(8, 9, 10),
    shortcutStatus: Boolean? = null,
    onShortcutKeysChange: (List<Int>) -> Unit = {},
    shortcutAction: Int = -1,
    shortcutRevision: Int = 0,
    compact: Boolean,
    pinned: Boolean,
    onTogglePinned: () -> Unit,
    onToggleCompact: () -> Unit,
    noteRepository: NoteRepository? = null,
    twitchChatClient: TwitchChatClient = InactiveTwitchChatClient,
    kickChatClient: KickChatClient = InactiveKickChatClient,
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
    initialProfile: dev.rocky.core.agent.InterventionProfile? = null,
    onProfileChange: (dev.rocky.core.agent.InterventionProfile) -> Unit = {},
    initialFilters: dev.rocky.core.live.ChatFilterConfiguration = dev.rocky.core.live.ChatFilterConfiguration(),
    onFiltersChange: (dev.rocky.core.live.ChatFilterConfiguration) -> Unit = {},
    initialAutomaticAnalysis: Boolean = false,
    onAutomaticAnalysisChange: (Boolean) -> Unit = {},
    initialVoiceConfiguration: VoiceConfiguration = VoiceConfiguration(),
    onVoiceConfigurationChange: (VoiceConfiguration) -> Unit = {},
    onChooseWhisperExecutable: () -> String? = { null },
    onChooseWhisperModel: () -> String? = { null },
    initialTwitchClientId: String = "",
    onTwitchClientIdChange: (String) -> Unit = {},
    onOpenTwitchAuthorization: (String) -> Unit = {},
    initialKickConfiguration: KickConfiguration = KickConfiguration(),
    onKickConfigurationChange: (KickConfiguration) -> Unit = {},
    onOpenKickAuthorization: (String) -> Unit = {},
    onExportNotes: (List<LiveNote>) -> Boolean = { false },
    onExportIdeas: (List<LiveIdea>) -> Boolean = { false },
    onSettingsVisibilityChanged: (Boolean) -> Unit = {},
    onBackup: (List<LiveNote>) -> Boolean = { false },
    onChooseImport: () -> List<LiveNote>? = { null },
    onCheckUpdate: () -> dev.rocky.core.updates.AvailableUpdate? = { null },
    onExportDiagnostic: (String) -> Boolean = { false },
    initialCheckUpdatesOnStart: Boolean = false,
    onCheckUpdatesOnStartChange: (Boolean) -> Unit = {},
    onRegisterSessionEnd: ((() -> Boolean) -> Unit) = {},
    onOpenGuide: (String) -> Unit = {},
    onOpenDataDirectory: () -> Unit = {},
    onResetSettings: () -> Unit = {},
    onRemoveManagedVoiceModel: () -> Unit = {},
    dataDirectoryLabel: String = "",
    buildLabel: String = "development",
    initialStorageNotice: String? = null,
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
        var preflightOpen by remember { mutableStateOf(false) }
        var historyOpen by remember { mutableStateOf(false) }
        var silenced by remember { mutableStateOf(false) }
        var waitingForVoiceCommand by remember { mutableStateOf(false) }
        val twitch = remember(twitchChatClient) { TwitchLiveState(twitchChatClient, currentTimeMillis) }
        val kick = remember(kickChatClient) { KickLiveState(kickChatClient, currentTimeMillis) }
        val ai = remember(aiSuggestionClient) {
            AiSuggestionState(
                aiSuggestionClient, initialAiConfiguration,
                initialAutomaticAnalysis, onAutomaticAnalysisChange,
                initialProfile, onProfileChange, initialFilters, onFiltersChange,
                onConfigurationChange = onAiConfigurationChange,
            )
        }
        LaunchedEffect(Unit) { initialStorageNotice?.let { ai.showNotice(it) } }
        val agent = remember { AgentState(initialAgentConfiguration, onAgentConfigurationChange) }
        val voice = remember(voiceService) {
            VoiceState(
                voiceService,
                initialVoiceConfiguration,
                onConfigurationChange = onVoiceConfigurationChange,
            )
        }
        val aiScope = rememberCoroutineScope()
        val updates = remember { UpdateState(onCheckUpdate) }
        var checkUpdatesOnStart by remember { mutableStateOf(initialCheckUpdatesOnStart) }
        LaunchedEffect(Unit) { if (checkUpdatesOnStart) updates.check(aiScope) }
        val mainContentScrollState = rememberScrollState()
        var twitchClientId by remember { mutableStateOf(initialTwitchClientId) }
        var kickConfiguration by remember { mutableStateOf(initialKickConfiguration) }
        val transientNotes = remember { TransientNoteRepository() }
        val resolvedNoteRepository = noteRepository ?: transientNotes
        val localNotes = remember(resolvedNoteRepository) { LocalNotesState(resolvedNoteRepository) }
        val workspace = remember(localNotes) { LiveWorkspace(localNotes) }
        var queueOpen by remember { mutableStateOf(false) }
        androidx.compose.runtime.DisposableEffect(workspace) {
            onRegisterSessionEnd { workspace.finish(currentTimeLabel()) }
            onDispose { onRegisterSessionEnd { true } }
        }
        val liveConnected = twitch.phase == TwitchConnectionPhase.Connected || kick.isConnected
        val liveActive = twitch.isRealSession || kick.isActive
        val visibleMessages = (twitch.messages + kick.messages).sortedBy(ChatMessage::receivedAtMillis)
        val visibleMessageCount = twitch.totalMessages + kick.totalMessages
        fun recentMessages() = (
            twitch.messagesReceivedWithin(VOICE_CHAT_WINDOW_MILLIS) +
                kick.messagesReceivedWithin(VOICE_CHAT_WINDOW_MILLIS)
            ).sortedBy(ChatMessage::receivedAtMillis)
        LaunchedEffect(twitch.phase, twitch.sessionId, kick.phase, kick.sessionId) {
            when {
                twitch.phase == TwitchConnectionPhase.Connected -> workspace.start(
                    twitch.sessionId, "@${twitch.account?.login} · ${currentTimeLabel()}",
                    twitch.startedAtMillis ?: currentTimeMillis())
                kick.isConnected -> workspace.start(
                    kick.sessionId, "@${kick.account?.username} · ${currentTimeLabel()}",
                    kick.startedAtMillis ?: currentTimeMillis())
                !liveActive -> workspace.finish(currentTimeLabel())
            }
        }
        val sessionStatus = when {
            liveConnected -> LiveSessionStatus.Running
            twitch.phase == TwitchConnectionPhase.Failed || kick.phase == KickConnectionPhase.Failed -> LiveSessionStatus.Ended
            else -> LiveSessionStatus.Stopped
        }
        val visibleSuggestion = ai.suggestion
        val visiblePlatforms = platformStatuses(twitch, kick)

        LaunchedEffect(
            twitch.phase,
            twitch.totalMessages,
            kick.phase,
            kick.totalMessages,
            ai.automaticAnalysis,
            ai.analysisRevision,
            ai.profile,
        ) {
            if (liveConnected && ai.automaticAnalysis) {
                delay(ai.automaticAnalysisDelay(currentTimeMillis(), ai.profile.intervalMillis))
                ai.analyze(
                    aiScope,
                    recentMessages(),
                    automatic = true,
                    agent = agent.configuration.copy(language = language),
                    automaticTimeMillis = currentTimeMillis(),
                )
            }
        }

        LaunchedEffect(twitch.phase, kick.phase) {
            while (liveActive) {
                delay(METRICS_REFRESH_MILLIS)
                twitch.refreshMetrics()
                kick.refreshMetrics()
            }
        }

        LaunchedEffect(twitch.totalMessages, kick.totalMessages, ai.filters, workspace.sessionId) {
            if (workspace.sessionId.isNotBlank()) {
                workspace.questions.collect(dev.rocky.core.live.filterChat(visibleMessages, ai.filters).messages,
                    workspace.sessionId, workspace.label, currentTimeLabel(), workspace.offset(currentTimeMillis()),
                    visibleMessages.map { it.id }.toSet())
            }
        }
        LaunchedEffect(visibleSuggestion?.id, silenced) {
            visibleSuggestion?.let { voice.speakSuggestion(aiScope, it.id, it.text, silenced) }
        }

        LaunchedEffect(sessionStatus) {
            if (sessionStatus != LiveSessionStatus.Running) {
                waitingForVoiceCommand = false
                voice.resetSession()
                if (liveActive && ai.generating) ai.cancelAnalysis()
            }
        }

        LaunchedEffect(language) { voice.updateLanguage(language) }

        fun spokenText(english: String, portuguese: String) =
            if (language == RockyLanguage.English) english else portuguese

        val analyzeVoiceCommand: (String) -> Unit = { request ->
            val recentMessages = recentMessages()
            ai.analyze(
                scope = aiScope,
                messages = recentMessages,
                streamerRequest = request,
                agent = agent.configuration.copy(language = language),
                messageLimit = recentMessages.size,
                onComplete = { suggestion ->
                    if (voice.listenerEnabled) {
                        if (suggestion == null) {
                            voice.speakAcknowledgement(
                                aiScope,
                                spokenText("I could not check the chat. I will keep listening.", "Não consegui consultar o chat agora. Vou continuar ouvindo."),
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
        fun saveRecord(note: LiveNote): Boolean = localNotes.save(workspace.decorate(note, currentTimeMillis()))
        fun finishLive(): Boolean {
            if (!workspace.finish(currentTimeLabel())) return false
            voice.resetSession(); ai.resetSession(); twitch.disconnect(); kick.disconnect()
            return true
        }
        fun saveAnswer(entry: ConversationEntry, target: VoiceSaveTarget): Boolean {
            val note = suggestionNote(entry.answer, entry.sources, currentTimeLabel()).copy(
                id = "${entry.answer.id}-${target.name}",
                tag = if (target == VoiceSaveTarget.Idea) IDEA_TAG else "SUGESTÃO IA",
            )
            if (!saveRecord(note)) return false
            return true
        }
        fun saveCurrentSuggestion(target: VoiceSaveTarget): Boolean {
            val answer = ai.suggestion ?: return false
            return saveAnswer(ConversationEntry("", answer, ai.suggestionSources), target)
        }

        val submitVoiceCommand: (String) -> Unit = { command ->
            waitingForVoiceCommand = false
            val marker = momentCommand(command)
            val dictated = dictatedNote(command)
            val target = voiceSaveTarget(command)
            if (marker != null) {
                val saved = workspace.sessionId.isNotBlank() && saveRecord(LiveNote(
                    "moment-${kotlin.random.Random.nextLong()}", marker, currentTimeLabel(), dev.rocky.core.live.MARKER_TAG))
                voice.speakAcknowledgement(aiScope,
                    if (saved) spokenText("Moment saved.", "Momento salvo.")
                    else spokenText("Connect a live first, or check the records folder.", "Conecte uma live primeiro ou verifique a pasta de registros."),
                    silenced, voice::resumeListener)
            } else if (dictated != null) {
                val saved = saveRecord(LiveNote(
                    "manual-${kotlin.random.Random.nextLong()}", dictated.text, currentTimeLabel(),
                    if (dictated.target == VoiceSaveTarget.Idea) IDEA_TAG else "MANUAL",
                ))
                voice.speakAcknowledgement(aiScope,
                    if (saved) spokenText("Saved.", "Salvo.") else spokenText("Could not save.", "Não foi possível salvar."),
                    silenced, voice::resumeListener)
            } else if (target != null) {
                val hadSuggestion = ai.suggestion != null
                val saved = saveCurrentSuggestion(target)
                val acknowledgement = when {
                    saved && target == VoiceSaveTarget.Idea -> spokenText("Idea saved.", "Ideia salva.")
                    saved -> spokenText("Note saved.", "Nota salva.")
                    !hadSuggestion -> spokenText("There is no answer to save yet.", "Ainda não há uma resposta para salvar.")
                    else -> spokenText("Could not save. Please try again.", "Não foi possível salvar. Tente novamente.")
                }
                voice.speakAcknowledgement(aiScope, acknowledgement, silenced, voice::resumeListener)
            } else {
                voice.speakAcknowledgement(aiScope, spokenText("I will check the chat.", "Vou verificar o chat."), silenced) {
                    if (voice.listenerEnabled) analyzeVoiceCommand(command)
                }
            }
        }
        val handleVoiceRequest: (String) -> Unit = { transcript ->
            val directCommand = extractRockyCommand(transcript, agent.displayName)
            when {
                directCommand != null -> submitVoiceCommand(directCommand)
                waitingForVoiceCommand -> submitVoiceCommand(transcript.trim())
                containsRockyWakeWord(transcript, agent.displayName) -> {
                    waitingForVoiceCommand = true
                    voice.speakAcknowledgement(aiScope, spokenText("I am listening.", "Estou ouvindo."), silenced, voice::resumeListener)
                }
                else -> voice.resumeListener()
            }
        }

        LaunchedEffect(shortcutRevision) {
            if (shortcutRevision > 0) when (shortcutAction) {
                0 -> {
                    voice.enableListener(aiScope, handleVoiceRequest)
                    voice.cancelCapture()
                    voice.startCapture(aiScope) { transcript ->
                        submitVoiceCommand(extractRockyCommand(transcript, agent.displayName) ?: transcript.trim())
                    }
                }
                1 -> {
                    silenced = !silenced
                    if (silenced) voice.interruptSpeech()
                }
            }
        }

        LaunchedEffect(twitch.phase, kick.phase, voice.transcriptionReady) {
            if (liveConnected && voice.transcriptionReady) {
                voice.enableListener(aiScope, handleVoiceRequest)
            }
        }

        CompositionLocalProvider(LocalRockyLanguage provides language) {
        if (queueOpen) QuestionQueueDialog(localNotes, workspace.sessionId, { queueOpen = false })
        workspace.summary?.let { text ->
            androidx.compose.material.AlertDialog(onDismissRequest = { workspace.summary = null },
                title = { Text(tr("Saved live summary", "Resumo da live salvo")) },
                text = { Text(text, modifier = Modifier.verticalScroll(rememberScrollState())) },
                confirmButton = { androidx.compose.material.TextButton(onClick = { workspace.summary = null }) { Text("OK") } })
        }
        if (preflightOpen) PreflightDialog(aiScope, twitch, kick, ai, voice, agent.displayName,
            onConfigure = { section ->
                preflightOpen = false; settingsOpen = true; settingsSection = section
                onSettingsVisibilityChanged(true)
            },
            onOpenGuide = onOpenGuide,
            onDismiss = { preflightOpen = false },
        )
        if (historyOpen) ConversationHistory(
            ai.history.toList(), notice = localNotes.notice, canUndo = localNotes.undoSaveId != null,
            onUndo = localNotes::undoSave, onDismiss = { historyOpen = false },
            onRepeat = { request ->
                voice.stopSpeaking()
                ai.analyze(aiScope, recentMessages(), streamerRequest = request,
                    agent = agent.configuration.copy(language = language))
            },
            onSave = { entry, target -> saveAnswer(entry, target) },
        )
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
                    onPreflight = { preflightOpen = true },
                    onTogglePinned = onTogglePinned,
                    onToggleCompact = onToggleCompact,
                    onOpenSettings = {
                        settingsOpen = true
                        onSettingsVisibilityChanged(true)
                    },
                )
                Divider(color = RockyColors.Divider)
                if (!updates.dismissed && sessionStatus != LiveSessionStatus.Running) updates.available?.let { update ->
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                        androidx.compose.material.TextButton(onClick = { onOpenGuide(update.url) }) {
                            Text(tr("Update available", "Atualização disponível") + " · ${update.version}")
                        }
                        androidx.compose.material.TextButton(onClick = { updates.dismissed = true }) { Text(tr("Later", "Depois")) }
                    }
                }
                if (!historyOpen) localNotes.notice?.let { notice ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                        Text(notice, modifier = Modifier.weight(1f), style = MaterialTheme.typography.caption, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        if (localNotes.undoSaveId != null) androidx.compose.material.TextButton(onClick = localNotes::undoSave) {
                            Text(tr("Undo", "Desfazer"))
                        }
                    }
                }
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
                                SettingsSection.Data -> DataSettings(
                                    localNotes, dataDirectoryLabel, buildLabel,
                                    onBackup = onBackup, onChooseImport = onChooseImport,
                                    updates = updates, onOpenGuide = onOpenGuide, onExportDiagnostic = onExportDiagnostic,
                                    checkUpdatesOnStart = checkUpdatesOnStart,
                                    onCheckUpdatesOnStart = { checkUpdatesOnStart = it; onCheckUpdatesOnStartChange(it) },
                                    diagnosticReport = {
                                        listOf("Rocky $buildLabel", "Twitch: ${twitch.phase}", "Kick: ${kick.phase}", "AI: ${ai.configuration.provider}",
                                            "AI configured: ${ai.isReady}", "Completed requests: ${ai.completedRequests}",
                                            "Last request ms: ${ai.lastDurationMillis}", "Filtered messages: ${ai.filteredCount}",
                                            "Voice ready: ${voice.transcriptionReady}", "Microphone active: ${voice.capturing}",
                                            "Saved records: ${localNotes.notes.size}", "Database load failed: ${localNotes.loadFailed}",
                                            "No keys, channel names, paths or message contents included.").joinToString("\n")
                                    },
                                    onOpenDataDirectory = onOpenDataDirectory,
                                    onExportNotes = { localNotes.export { notes -> onExportNotes(notes.filter { it.tag != IDEA_TAG }) } },
                                    onResetSettings = {
                                        if (finishLive()) {
                                            onResetSettings()
                                            ai.updateApiKey("")
                                        }
                                    },
                                    onRemoveModel = {
                                        voice.disableListener(); voice.cancelTranscriptionSetup(); voice.cancelCapture()
                                        onRemoveManagedVoiceModel()
                                        voice.updateWhisperModel("")
                                    },
                                )
                                SettingsSection.Ai -> AiSettings(ai)
                                SettingsSection.Voice -> Column {
                                    ShortcutSettings(shortcutKeys, shortcutStatus, onShortcutKeysChange)
                                    VoiceSettings(voice, agent.displayName, onChooseWhisperExecutable, onChooseWhisperModel)
                                }
                                SettingsSection.Platforms -> PlatformSettings(
                                    clientId = twitchClientId,
                                    onClientIdChange = { value ->
                                        twitchClientId = value
                                        onTwitchClientIdChange(value)
                                    },
                                    twitch = twitch,
                                    onConnect = {
                                        if (finishLive()) {
                                            silenced = false
                                            twitch.connect(twitchClientId)
                                        }
                                    },
                                    onDisconnect = {
                                        silenced = false
                                        finishLive()
                                    },
                                    onOpenBrowser = onOpenTwitchAuthorization,
                                    kickConfiguration = kickConfiguration,
                                    kick = kick,
                                    onConnectKick = { configuration ->
                                        if (finishLive()) {
                                            silenced = false
                                            kickConfiguration = configuration
                                            onKickConfigurationChange(configuration)
                                            kick.connect(configuration)
                                        }
                                    },
                                    onDisconnectKick = {
                                        silenced = false
                                        finishLive()
                                    },
                                    onOpenKickBrowser = onOpenKickAuthorization,
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
                                twitchConnected = liveConnected,
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
                                finishLive()
                            }
                        }
                        if (kick.isActive) {
                            KickSessionControls(kick) {
                                silenced = false
                                finishLive()
                            }
                        }
                        Divider(color = RockyColors.Divider)
                        PlatformStrip(visiblePlatforms)
                        Divider(color = RockyColors.Divider)
                        LiveSummary(
                            agentName = agent.displayName,
                            suggestion = visibleSuggestion,
                            sourceCounts = ai.suggestionSources.groupingBy(ChatMessage::platform).eachCount(),
                            sessionStatus = sessionStatus,
                            sessionAvailable = liveActive,
                            suggestionSaved = false,
                            silenced = silenced,
                            speaking = voice.speaking,
                            generatingSuggestion = ai.generating,
                            aiConfigured = ai.isReady,
                            canAnalyze = liveConnected && recentMessages().isNotEmpty() && ai.isReady,
                            analysisStatus = ai.status,
                            evidence = ai.suggestionSources.map(::messageEvidence),
                            onSaveNote = { saveCurrentSuggestion(VoiceSaveTarget.Note) },
                            onAnalyze = { ai.analyze(aiScope, recentMessages(), agent = agent.configuration.copy(language = language)) },
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
                                    onQueue = { queueOpen = true },
                                    pendingQuestions = localNotes.notes.count { it.tag == dev.rocky.core.live.QUESTION_TAG && it.sessionId == workspace.sessionId && !it.completed },
                                    filteredCount = ai.filteredCount,
                                    onHistory = { historyOpen = true },
                                    streamerSpeech = voice.transcript,
                                    showTextRequest = true,
                                    textRequestEnabled = liveConnected && ai.isReady && ai.acceptsDirectRequest,
                                    analyzing = ai.generating,
                                    hasCaptureGaps = twitch.hasCaptureGaps,
                                    analysisStatus = ai.status,
                                    performanceNotice = ai.lastDurationMillis?.let {
                                        "${it} ms · ${ai.completedRequests} análises · ${ai.reportedTokens} tokens informados (parcial)"
                                    },
                                    onCancelAnalysis = { ai.cancelAnalysis(); voice.resumeListener() },
                                    onTextRequest = { request ->
                                        voice.stopSpeaking()
                                        ai.analyze(aiScope, recentMessages(), streamerRequest = request, agent = agent.configuration.copy(language = language))
                                    },
                                )
                                MainSection.Support -> SupportContent()
                                MainSection.Notes -> NotesContent(
                                    notes = localNotes.notes.filter { it.tag != IDEA_TAG && it.tag != dev.rocky.core.live.QUESTION_TAG },
                                    loadFailed = localNotes.loadFailed,
                                    onReload = localNotes::reload,
                                    notice = localNotes.notice,
                                    onCreate = { text ->
                                        saveRecord(LiveNote("manual-${kotlin.random.Random.nextLong()}", text, currentTimeLabel(),
                                            if (mainSection == MainSection.Ideas) IDEA_TAG else "MANUAL"))
                                    },
                                    onUpdate = localNotes::update,
                                    onDelete = localNotes::delete,
                                    onExport = {
                                        localNotes.export { notes -> onExportNotes(notes.filter { it.tag != IDEA_TAG }) }
                                    },
                                )
                                MainSection.Ideas -> NotesContent(
                                    notes = localNotes.notes.filter { it.tag == IDEA_TAG },
                                    title = "Ideias da live",
                                    ideas = true,
                                    notice = localNotes.notice,
                                    loadFailed = localNotes.loadFailed,
                                    onReload = localNotes::reload,
                                    onCreate = { text ->
                                        saveRecord(LiveNote("manual-${kotlin.random.Random.nextLong()}", text, currentTimeLabel(),
                                            if (mainSection == MainSection.Ideas) IDEA_TAG else "MANUAL"))
                                    },
                                    onUpdate = localNotes::update,
                                    onDelete = localNotes::delete,
                                    onExport = {
                                        localNotes.export { notes ->
                                            onExportIdeas(notes.filter { it.tag == IDEA_TAG }.map {
                                                LiveIdea(it.text, it.timestamp, it.tag)
                                            })
                                        }
                                    },
                                )
                                MainSection.Pulse -> PulseContent(
                                    samples = if (kick.isActive) kick.pulse.toList() else twitch.pulse.toList(),
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
                            viewerCount = listOfNotNull(twitch.viewerCount, kick.viewerCount).takeIf { it.isNotEmpty() }?.sum(),
                            messagesPerMinute = twitch.messagesPerMinute + kick.messagesPerMinute,
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

private object InactiveKickChatClient : KickChatClient {
    override fun connect(configuration: KickConfiguration, listener: KickConnectionListener) = Unit
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
    LiveSessionStatus.Stopped -> "Conecte Twitch ou Kick"
    LiveSessionStatus.Running -> "Chat da live conectado"
    LiveSessionStatus.Ended -> "Conexão da live encerrada"
}

private fun platformStatuses(twitch: TwitchLiveState, kick: KickLiveState): List<PlatformStatus> =
    listOf(
        PlatformStatus(
            name = "Twitch",
            account = twitch.account?.let { "@${it.login}" } ?: "Não conectada",
            audience = twitch.viewerCount?.toString() ?: "—",
            messagesPerMinute = twitch.messagesPerMinute,
            colorKey = PlatformColor.Twitch,
            enabled = twitch.phase != TwitchConnectionPhase.Failed,
        ),
        PlatformStatus(
            name = "Kick",
            account = kick.account?.let { "@${it.username}" } ?: "Não conectada",
            audience = kick.viewerCount?.toString() ?: "—",
            messagesPerMinute = kick.messagesPerMinute,
            colorKey = PlatformColor.Kick,
            enabled = kick.phase != KickConnectionPhase.Failed,
        ),
        PlatformStatus("YouTube", "Em breve", "0", 0, PlatformColor.Offline, enabled = false),
        PlatformStatus("Facebook", "Em breve", "0", 0, PlatformColor.Offline, enabled = false),
    )

private val AgentConfiguration.analysisIntervalMillis: Long
    get() = 600_000L / interventionsPerTenMinutes.coerceIn(1, 9)

private const val METRICS_REFRESH_MILLIS = 5_000L
private const val VOICE_CHAT_WINDOW_MILLIS = 120_000L
