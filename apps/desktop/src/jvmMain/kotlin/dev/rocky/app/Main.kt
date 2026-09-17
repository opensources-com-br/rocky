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
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.rocky.data.notes.RecoverableNoteRepository
import dev.rocky.data.ai.DesktopAiSuggestionClient
import dev.rocky.data.kick.DesktopKickChatClient
import dev.rocky.data.twitch.DesktopTwitchChatClient
import dev.rocky.data.youtube.DesktopYouTubeChatClient
import dev.rocky.data.facebook.DesktopFacebookChatClient
import dev.rocky.data.tiktok.DesktopTikTokChatClient
import dev.rocky.platform.desktop.RockyDesktopPaths
import dev.rocky.platform.desktop.AgentDesktopPreferences
import dev.rocky.platform.desktop.AiDesktopPreferences
import dev.rocky.platform.desktop.TwitchDesktopPreferences
import dev.rocky.platform.desktop.DesktopVoiceService
import dev.rocky.platform.desktop.FirstUseDesktopPreferences
import dev.rocky.platform.desktop.LanguageDesktopPreferences
import dev.rocky.platform.desktop.KickDesktopPreferences
import dev.rocky.platform.desktop.VoiceDesktopPreferences
import dev.rocky.platform.desktop.YouTubeDesktopPreferences
import dev.rocky.platform.desktop.FacebookDesktopPreferences
import dev.rocky.platform.desktop.TikTokDesktopPreferences
import dev.rocky.platform.desktop.exportIdeasAsMarkdown
import dev.rocky.platform.desktop.exportNotesAsMarkdown
import dev.rocky.platform.desktop.openInBrowser
import dev.rocky.platform.desktop.chooseDesktopFile
import dev.rocky.ui.window.RockyMainWindowHost
import dev.rocky.ui.window.RockySettingsWindowHost
import dev.rocky.ui.window.RockyWindow
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.compose.resources.painterResource
import dev.rocky.platform.desktop.DesktopWindowPreferences
import dev.rocky.platform.desktop.DesktopShortcuts
import dev.rocky.platform.desktop.ShortcutPreferences
import dev.rocky.platform.desktop.ShortcutConfiguration
import androidx.compose.runtime.rememberUpdatedState
import java.awt.Taskbar
import java.awt.SystemTray
import javax.imageio.ImageIO
import java.awt.Dimension
import java.awt.Frame
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicReference
import dev.rocky.apps.desktop.generated.resources.Res
import dev.rocky.apps.desktop.generated.resources.rocky_tray

fun main() {
    if (isMacOs()) {
        System.setProperty("apple.awt.UIElement", "true")
        System.setProperty("apple.awt.enableTemplateImages", "true")
    }
    runRockyApplication()
}

private fun runRockyApplication() = application {
    val usesTray = shouldUseTray(System.getProperty("os.name"), SystemTray.isSupported())
    val appIcon = remember {
        ImageIO.read(requireNotNull(Thread.currentThread().contextClassLoader.getResource("rocky.png")))
    }
    val trayIcon = if (isWindows()) BitmapPainter(appIcon.toComposeImageBitmap()) else painterResource(Res.drawable.rocky_tray)
    LaunchedEffect(Unit) {
        if (!usesTray && Taskbar.isTaskbarSupported()) {
            val taskbar = Taskbar.getTaskbar()
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) taskbar.iconImage = appIcon
        }
    }
    var finishSession by remember { mutableStateOf<() -> Boolean>({ true }) }
    val windowState = rememberWindowState(size = ExpandedSize)
    val layoutLifecycle = remember { DesktopLayoutLifecycle() }
    var pinned by remember { mutableStateOf(DesktopWindowPreferences.pinned) }
    val windowLifecycle = remember { DesktopWindowLifecycle(usesTray) }
    val desktopWindow = remember { AtomicReference<Frame?>(null) }
    val settingsDesktopWindow = remember { AtomicReference<Frame?>(null) }
    val settingsWindowState = rememberWindowState(size = SettingsSize)
    var shortcutConfiguration by remember { mutableStateOf(ShortcutPreferences.configuration) }
    var shortcutStatus by remember { mutableStateOf<Boolean?>(null) }
    var shortcutAction by remember { mutableStateOf(-1) }
    var shortcutRevision by remember { mutableStateOf(0) }
    val noteRepository = remember { RecoverableNoteRepository(RockyDesktopPaths.notesDatabase) }
    val twitchClient = remember { DesktopTwitchChatClient() }
    val kickClient = remember { DesktopKickChatClient() }
    val youtubeClient = remember { DesktopYouTubeChatClient() }
    val facebookClient = remember { DesktopFacebookChatClient() }
    val tiktokClient = remember { DesktopTikTokChatClient() }
    val aiClient = remember { DesktopAiSuggestionClient() }
    val voiceService = remember { dev.rocky.data.voice.ProviderVoiceService(
        DesktopVoiceService(), dev.rocky.platform.desktop.DesktopPcmPlayback(),
    ) }
    val updateInstaller = remember {
        dev.rocky.data.updates.DesktopUpdateInstaller(RockyDesktopPaths.notesDatabase.parent.resolve("updates"))
    }

    DisposableEffect(noteRepository, twitchClient, kickClient, youtubeClient, facebookClient, tiktokClient, aiClient, voiceService) {
        onDispose {
            voiceService.close()
            aiClient.close()
            twitchClient.close()
            kickClient.close()
            youtubeClient.close()
            facebookClient.close()
            tiktokClient.close()
            noteRepository.close()
        }
    }

    fun showRocky() {
        windowLifecycle.showMain()
        windowState.isMinimized = false
        desktopWindow.get()?.apply {
            isVisible = true
            toFront()
            requestFocus()
        }
    }

    fun showSettings() {
        windowLifecycle.requestSettings()
        settingsWindowState.isMinimized = false
        settingsDesktopWindow.get()?.takeIf { it.isVisible }?.apply {
            toFront()
            requestFocus()
        }
    }

    fun quitRocky() {
        if (finishSession()) exitApplication()
    }

    fun activeWindow(): Frame = settingsDesktopWindow.get()?.takeIf { it.isActive }
        ?: desktopWindow.get()
        ?: error("Rocky window is not ready")

    val mainWindowHost: RockyMainWindowHost = { content ->
        Window(
            onCloseRequest = { if (windowLifecycle.closeMain()) quitRocky() },
            state = windowState,
            visible = windowLifecycle.mainVisible,
            title = "Rocky",
            icon = BitmapPainter(appIcon.toComposeImageBitmap()),
            resizable = true,
            alwaysOnTop = pinned,
        ) {
            LaunchedEffect(window) {
                desktopWindow.set(window)
                window.minimumSize = Dimension(340, 180)
            }
            val persistBounds by rememberUpdatedState(!layoutLifecycle.compact)
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
                        windowLifecycle.toggleMain()
                        if (windowLifecycle.mainVisible) { windowState.isMinimized = false; window.toFront() }
                    } else { shortcutAction = action; shortcutRevision += 1 }
                }, onStatus = { shortcutStatus = it })
                shortcuts.start()
                onDispose { shortcuts.close() }
            }
            content()
        }
    }

    val settingsWindowHost: RockySettingsWindowHost? = if (usesTray) {
        { visible, onCloseRequest, content ->
            Window(
                onCloseRequest = onCloseRequest,
                state = settingsWindowState,
                visible = visible,
                title = "Rocky Settings",
                icon = BitmapPainter(appIcon.toComposeImageBitmap()),
                resizable = true,
            ) {
                LaunchedEffect(window) {
                    settingsDesktopWindow.set(window)
                    window.minimumSize = Dimension(700, 560)
                }
                content()
            }
        }
    } else {
        null
    }

    if (usesTray) {
        Tray(
            icon = trayIcon,
            tooltip = "Rocky",
            onAction = ::showRocky,
            menu = {
                Item("Rocky", onClick = ::showRocky)
                Item(traySettingsLabel(System.getProperty("os.name")), onClick = ::showSettings)
                Separator()
                Item("Quit", onClick = ::quitRocky)
            },
        )
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
            compact = layoutLifecycle.compact,
            pinned = pinned,
            noteRepository = noteRepository,
            twitchChatClient = twitchClient,
            kickChatClient = kickClient,
            youtubeChatClient = youtubeClient,
            facebookChatClient = facebookClient,
            tiktokChatClient = tiktokClient,
            aiSuggestionClient = aiClient,
            voiceService = voiceService,
            initialAgentConfiguration = AgentDesktopPreferences.configuration,
            onAgentConfigurationChange = { AgentDesktopPreferences.configuration = it },
            initialAiConfiguration = AiDesktopPreferences.configuration,
            initialStorageNotice = AiDesktopPreferences.storageNotice,
            onBackup = { notes -> dev.rocky.platform.desktop.exportRecordFile(activeWindow(),
                dev.rocky.data.notes.RecordBackup.encode(notes), "rocky-backup.json") },
            onChooseImport = { dev.rocky.platform.desktop.chooseRecordBackup(activeWindow())?.let(dev.rocky.data.notes.RecordBackup::decode) },
            onCheckUpdate = { dev.rocky.data.updates.ReleaseChecker().check(System.getProperty("rocky.version", "development")) },
            updateInstaller = updateInstaller,
            onExportDiagnostic = { report -> dev.rocky.platform.desktop.exportRecordFile(activeWindow(), report, "rocky-diagnostics.txt") },
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
                chooseDesktopFile(activeWindow(), "Selecione o executável whisper-cli")
            },
            onChooseWhisperModel = {
                chooseDesktopFile(activeWindow(), "Selecione o modelo GGML", setOf("bin"))
            },
            initialTwitchClientId = TwitchDesktopPreferences.clientId.ifBlank {
                System.getProperty("rocky.twitch.clientId").orEmpty()
            },
            onTwitchClientIdChange = { TwitchDesktopPreferences.clientId = it },
            onOpenTwitchAuthorization = { openInBrowser(it) },
            initialKickConfiguration = KickDesktopPreferences.configuration,
            onKickConfigurationChange = { KickDesktopPreferences.configuration = it },
            onOpenKickAuthorization = { openInBrowser(it) },
            initialYouTubeConfiguration = YouTubeDesktopPreferences.configuration,
            onYouTubeConfigurationChange = { YouTubeDesktopPreferences.configuration = it },
            onOpenYouTubeAuthorization = { openInBrowser(it) },
            initialFacebookConfiguration = FacebookDesktopPreferences.configuration,
            onFacebookConfigurationChange = { FacebookDesktopPreferences.configuration = it },
            onOpenFacebookAuthorization = { openInBrowser(it) },
            initialTikTokConfiguration = TikTokDesktopPreferences.configuration,
            onTikTokConfigurationChange = { TikTokDesktopPreferences.configuration = it },
            onExportNotes = { notes -> exportNotesAsMarkdown(activeWindow(), notes) },
            onExportIdeas = { ideas -> exportIdeasAsMarkdown(activeWindow(), ideas) },
            initialFirstUseOpen = !FirstUseDesktopPreferences.completed,
            onFirstUseFinished = { FirstUseDesktopPreferences.completed = true },
            initialLanguage = LanguageDesktopPreferences.language,
            onLanguageChange = { LanguageDesktopPreferences.language = it },
            currentTimeLabel = { OffsetDateTime.now().format(TimeFormatter) },
            currentTimeMillis = System::currentTimeMillis,
            settingsRequestRevision = windowLifecycle.settingsRevision,
            mainWindow = mainWindowHost,
            settingsWindow = settingsWindowHost,
            onTogglePinned = { pinned = !pinned; DesktopWindowPreferences.pinned = pinned },
            onToggleCompact = {
                if (!layoutLifecycle.compact) {
                    windowState.placement = WindowPlacement.Floating
                }
                windowState.size = layoutLifecycle.toggle(windowState.size)
            },
        )
}

internal val ExpandedSize = DpSize(462.dp, 900.dp)
internal val SettingsSize = DpSize(780.dp, 680.dp)
internal val CompactSize = DpSize(340.dp, 180.dp)
private val TimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX")
private const val MenuWidthPadding = "\u2003\u2003\u2003\u2003\u2003\u2003\u2003\u2003\u2003\u2003\u2003\u2003\u2003"
internal fun shouldUseTray(osName: String, systemTraySupported: Boolean): Boolean =
    systemTraySupported && (isMacOs(osName) || isWindows(osName))

internal fun traySettingsLabel(osName: String): String =
    "Settings" + if (isMacOs(osName)) MenuWidthPadding else ""

private fun isMacOs(osName: String = System.getProperty("os.name")): Boolean = osName.startsWith("Mac", true)
private fun isWindows(osName: String = System.getProperty("os.name")): Boolean = osName.startsWith("Windows", true)
