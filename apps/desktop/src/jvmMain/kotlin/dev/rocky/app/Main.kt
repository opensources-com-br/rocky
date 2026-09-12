package dev.rocky.app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.rocky.data.notes.RecoverableNoteRepository
import dev.rocky.data.ai.DesktopAiSuggestionClient
import dev.rocky.data.twitch.DesktopTwitchChatClient
import dev.rocky.platform.desktop.RockyDesktopPaths
import dev.rocky.platform.desktop.AgentDesktopPreferences
import dev.rocky.platform.desktop.AiDesktopPreferences
import dev.rocky.platform.desktop.TwitchDesktopPreferences
import dev.rocky.platform.desktop.DesktopVoiceService
import dev.rocky.platform.desktop.FirstUseDesktopPreferences
import dev.rocky.platform.desktop.LanguageDesktopPreferences
import dev.rocky.platform.desktop.VoiceDesktopPreferences
import dev.rocky.platform.desktop.exportIdeasAsMarkdown
import dev.rocky.platform.desktop.exportNotesAsMarkdown
import dev.rocky.platform.desktop.openInBrowser
import dev.rocky.platform.desktop.chooseDesktopFile
import dev.rocky.ui.window.RockyWindow
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import dev.rocky.platform.desktop.DesktopWindowPreferences
import dev.rocky.platform.desktop.DesktopShortcuts
import dev.rocky.platform.desktop.ShortcutPreferences
import dev.rocky.platform.desktop.ShortcutConfiguration
import androidx.compose.runtime.rememberUpdatedState
import java.awt.Taskbar
import javax.imageio.ImageIO
import java.awt.Dimension
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun main() = application {
    val appIcon = remember {
        ImageIO.read(requireNotNull(Thread.currentThread().contextClassLoader.getResource("rocky.png")))
    }
    LaunchedEffect(Unit) {
        if (Taskbar.isTaskbarSupported()) {
            val taskbar = Taskbar.getTaskbar()
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) taskbar.iconImage = appIcon
        }
    }
    var finishSession by remember { mutableStateOf<() -> Boolean>({ true }) }
    val windowState = rememberWindowState(size = ExpandedSize)
    var compact by remember { mutableStateOf(false) }
    var pinned by remember { mutableStateOf(DesktopWindowPreferences.pinned) }
    var settingsVisible by remember { mutableStateOf(false) }
    var windowVisible by remember { mutableStateOf(true) }
    var shortcutConfiguration by remember { mutableStateOf(ShortcutPreferences.configuration) }
    var shortcutStatus by remember { mutableStateOf<Boolean?>(null) }
    var shortcutAction by remember { mutableStateOf(-1) }
    var shortcutRevision by remember { mutableStateOf(0) }
    var previousSize by remember { mutableStateOf(ExpandedSize) }
    var mainSizeBeforeSettings by remember { mutableStateOf(ExpandedSize) }
    val noteRepository = remember { RecoverableNoteRepository(RockyDesktopPaths.notesDatabase) }
    val twitchClient = remember { DesktopTwitchChatClient() }
    val aiClient = remember { DesktopAiSuggestionClient() }
    val voiceService = remember { DesktopVoiceService() }

    DisposableEffect(noteRepository, twitchClient, aiClient, voiceService) {
        onDispose {
            voiceService.close()
            aiClient.close()
            twitchClient.close()
            noteRepository.close()
        }
    }

    Window(
        onCloseRequest = { if (finishSession()) exitApplication() },
        state = windowState,
        visible = windowVisible,
        title = "Rocky",
        icon = BitmapPainter(appIcon.toComposeImageBitmap()),
        resizable = true,
        alwaysOnTop = pinned,
    ) {
        LaunchedEffect(window) {
            window.minimumSize = Dimension(340, 180)
        }
        val persistBounds by rememberUpdatedState(!compact && !settingsVisible)
        DisposableEffect(window) {
            window.bounds = DesktopWindowPreferences.restore()
            val timer = javax.swing.Timer(350) {
                if (persistBounds && windowState.placement == WindowPlacement.Floating) DesktopWindowPreferences.save(window.bounds)
            }.apply { isRepeats = false }
            val listener = object : java.awt.event.ComponentAdapter() {
                override fun componentMoved(event: java.awt.event.ComponentEvent) { timer.restart() }
                override fun componentResized(event: java.awt.event.ComponentEvent) { timer.restart() }
            }
            window.addComponentListener(listener)
            onDispose {
                timer.stop()
                if (persistBounds && windowState.placement == WindowPlacement.Floating) DesktopWindowPreferences.save(window.bounds)
                window.removeComponentListener(listener)
            }
        }
        DisposableEffect(shortcutConfiguration) {
            shortcutStatus = null
            val shortcuts = DesktopShortcuts(shortcutConfiguration, onAction = { action ->
                if (action == 2) {
                    windowVisible = !windowVisible
                    if (windowVisible) { windowState.isMinimized = false; window.toFront() }
                } else { shortcutAction = action; shortcutRevision += 1 }
            }, onStatus = { shortcutStatus = it })
            shortcuts.start()
            onDispose { shortcuts.close() }
        }
        RockyWindow(
            shortcutKeys = shortcutConfiguration.keys,
            shortcutStatus = shortcutStatus,
            onShortcutKeysChange = { keys ->
                val config = ShortcutConfiguration(keys[0], keys[1], keys[2])
                if (config.valid) { ShortcutPreferences.configuration = config; shortcutConfiguration = config }
            },
            shortcutAction = shortcutAction,
            shortcutRevision = shortcutRevision,
            compact = compact,
            pinned = pinned,
            noteRepository = noteRepository,
            twitchChatClient = twitchClient,
            aiSuggestionClient = aiClient,
            voiceService = voiceService,
            initialAgentConfiguration = AgentDesktopPreferences.configuration,
            onAgentConfigurationChange = { AgentDesktopPreferences.configuration = it },
            initialAiConfiguration = AiDesktopPreferences.configuration,
            initialStorageNotice = AiDesktopPreferences.storageNotice,
            onBackup = { notes -> dev.rocky.platform.desktop.exportRecordFile(window,
                dev.rocky.data.notes.RecordBackup.encode(notes), "rocky-backup.json") },
            onChooseImport = { dev.rocky.platform.desktop.chooseRecordBackup(window)?.let(dev.rocky.data.notes.RecordBackup::decode) },
            onCheckUpdate = { dev.rocky.data.updates.ReleaseChecker().check(System.getProperty("rocky.version", "development")) },
            onExportDiagnostic = { report -> dev.rocky.platform.desktop.exportRecordFile(window, report, "rocky-diagnostics.txt") },
            initialCheckUpdatesOnStart = dev.rocky.platform.desktop.ExperiencePreferences.checkUpdatesOnStart,
            onCheckUpdatesOnStartChange = { dev.rocky.platform.desktop.ExperiencePreferences.checkUpdatesOnStart = it },
            onRegisterSessionEnd = { finishSession = it },
            onOpenGuide = ::openInBrowser,
            onOpenDataDirectory = { dev.rocky.platform.desktop.openRockyDataDirectory() },
            onResetSettings = { dev.rocky.platform.desktop.resetRockySettings() },
            onRemoveManagedVoiceModel = { dev.rocky.platform.desktop.removeManagedVoiceModel() },
            dataDirectoryLabel = RockyDesktopPaths.notesDatabase.parent.toString(),
            buildLabel = "${System.getProperty("rocky.version", "development")} · ${System.getProperty("rocky.commit", "unknown").take(12)} · ${System.getProperty("os.name")} ${System.getProperty("os.arch")}",
            onAiConfigurationChange = { AiDesktopPreferences.configuration = it },
            initialProfile = dev.rocky.platform.desktop.ExperiencePreferences.profile,
            onProfileChange = { dev.rocky.platform.desktop.ExperiencePreferences.profile = it },
            initialFilters = dev.rocky.platform.desktop.ExperiencePreferences.filters,
            onFiltersChange = { dev.rocky.platform.desktop.ExperiencePreferences.filters = it },
            initialAutomaticAnalysis = AiDesktopPreferences.automaticAnalysis,
            onAutomaticAnalysisChange = { AiDesktopPreferences.automaticAnalysis = it },
            initialVoiceConfiguration = VoiceDesktopPreferences.configuration,
            onVoiceConfigurationChange = { VoiceDesktopPreferences.configuration = it },
            onChooseWhisperExecutable = {
                chooseDesktopFile(window, "Selecione o executável whisper-cli")
            },
            onChooseWhisperModel = {
                chooseDesktopFile(window, "Selecione o modelo GGML", setOf("bin"))
            },
            initialTwitchClientId = TwitchDesktopPreferences.clientId.ifBlank {
                System.getProperty("rocky.twitch.clientId").orEmpty()
            },
            onTwitchClientIdChange = { TwitchDesktopPreferences.clientId = it },
            onOpenTwitchAuthorization = { openInBrowser(it) },
            onExportNotes = { notes -> exportNotesAsMarkdown(window, notes) },
            onExportIdeas = { ideas -> exportIdeasAsMarkdown(window, ideas) },
            initialFirstUseOpen = !FirstUseDesktopPreferences.completed,
            onFirstUseFinished = { FirstUseDesktopPreferences.completed = true },
            initialLanguage = LanguageDesktopPreferences.language,
            onLanguageChange = { LanguageDesktopPreferences.language = it },
            currentTimeLabel = { OffsetDateTime.now().format(TimeFormatter) },
            currentTimeMillis = System::currentTimeMillis,
            onTogglePinned = { pinned = !pinned; DesktopWindowPreferences.pinned = pinned },
            onToggleCompact = {
                if (compact) {
                    windowState.size = previousSize
                } else {
                    previousSize = windowState.size
                    windowState.placement = WindowPlacement.Floating
                    windowState.size = CompactSize
                }
                compact = !compact
            },
            onSettingsVisibilityChanged = { open ->
                settingsVisible = open
                if (open && compact) {
                    compact = false
                    windowState.size = previousSize
                }
                if (!compact) {
                    if (open) {
                        mainSizeBeforeSettings = windowState.size
                        windowState.size = SettingsSize
                    } else {
                        windowState.size = mainSizeBeforeSettings
                    }
                }
            },
        )
    }
}

private val ExpandedSize = DpSize(462.dp, 820.dp)
private val SettingsSize = DpSize(462.dp, 820.dp)
private val CompactSize = DpSize(340.dp, 180.dp)
private val TimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX")
