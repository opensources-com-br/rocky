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
    val windowState = rememberWindowState(size = ExpandedSize)
    var compact by remember { mutableStateOf(false) }
    var pinned by remember { mutableStateOf(false) }
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
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Rocky",
        icon = BitmapPainter(appIcon.toComposeImageBitmap()),
        resizable = true,
        alwaysOnTop = pinned,
    ) {
        LaunchedEffect(window) {
            window.minimumSize = Dimension(340, 180)
        }
        RockyWindow(
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
            onOpenDataDirectory = { dev.rocky.platform.desktop.openRockyDataDirectory() },
            onResetSettings = { dev.rocky.platform.desktop.resetRockySettings() },
            onRemoveManagedVoiceModel = { dev.rocky.platform.desktop.removeManagedVoiceModel() },
            dataDirectoryLabel = RockyDesktopPaths.notesDatabase.parent.toString(),
            buildLabel = "${System.getProperty("rocky.version", "development")} · ${System.getProperty("rocky.commit", "unknown").take(12)} · ${System.getProperty("os.name")} ${System.getProperty("os.arch")}",
            onAiConfigurationChange = { AiDesktopPreferences.configuration = it },
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
            onTogglePinned = { pinned = !pinned },
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
