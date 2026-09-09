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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.rocky.core.live.LiveNote
import dev.rocky.core.live.LiveSessionStatus
import dev.rocky.core.notes.NoteRepository
import dev.rocky.core.twitch.TwitchChatClient
import dev.rocky.core.twitch.TwitchConnectionListener
import dev.rocky.ui.theme.RockyColors
import dev.rocky.ui.theme.RockyTheme

@Composable
fun RockyWindow(
    compact: Boolean,
    pinned: Boolean,
    onTogglePinned: () -> Unit,
    onToggleCompact: () -> Unit,
    noteRepository: NoteRepository? = null,
    twitchChatClient: TwitchChatClient = InactiveTwitchChatClient,
    initialTwitchClientId: String = "",
    onTwitchClientIdChange: (String) -> Unit = {},
    onOpenTwitchAuthorization: (String) -> Unit = {},
    onExportNotes: (List<LiveNote>) -> Boolean = { false },
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
        var talking by remember { mutableStateOf(false) }
        val live = rememberSimulatedLiveState()
        val twitch = remember(twitchChatClient) { TwitchLiveState(twitchChatClient) }
        var twitchClientId by remember { mutableStateOf(initialTwitchClientId) }
        val transientNotes = remember { TransientNoteRepository() }
        val resolvedNoteRepository = noteRepository ?: transientNotes
        val localNotes = remember(resolvedNoteRepository) { LocalNotesState(resolvedNoteRepository) }

        Surface(
            modifier = Modifier.fillMaxSize().testTag("rocky-window"),
            color = RockyColors.Background,
        ) {
            Column {
                RockyHeader(
                    compact = compact,
                    pinned = pinned,
                    sessionStatus = live.status,
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
                        status = live.status,
                        messageCount = live.messages.size,
                        suggestion = live.suggestion?.text,
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
                                SettingsSection.Ai -> AiSettings()
                                SettingsSection.Voice -> VoiceSettings()
                                SettingsSection.Platforms -> PlatformSettings(samplePlatforms)
                            }
                        }
                    }
                    else -> {
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
                        Divider(color = RockyColors.Divider)
                        PlatformStrip(samplePlatforms)
                        Divider(color = RockyColors.Divider)
                        LiveSummary(
                            suggestion = live.suggestion,
                            sourceCounts = live.sourceCounts,
                            sessionStatus = live.status,
                            suggestionSaved = live.suggestionSaved,
                            silenced = silenced,
                            onSaveNote = {
                                val note = live.createNoteFromSuggestion()
                                if (note != null && localNotes.save(note)) {
                                    live.markSuggestionSaved()
                                    mainSection = MainSection.Notes
                                }
                            },
                            onNext = live::dismissSuggestion,
                            onSilence = { silenced = !silenced },
                        )
                        MainNavigation(mainSection) { mainSection = it }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            when (mainSection) {
                                MainSection.Conversation -> ConversationContent(live.messages)
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
                                MainSection.Ideas -> TimelineContent(mainSection)
                                MainSection.Pulse -> PulseContent(samplePlatforms)
                            }
                        }
                        Divider(color = RockyColors.Divider)
                        AssistantFooter(active = talking) { talking = !talking }
                    }
                }
            }
        }
    }
}

private object InactiveTwitchChatClient : TwitchChatClient {
    override fun connect(clientId: String, listener: TwitchConnectionListener) = Unit

    override fun disconnect() = Unit

    override fun close() = Unit
}

@Composable
private fun CompactContent(
    status: LiveSessionStatus,
    messageCount: Int,
    suggestion: String?,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)) {
        Text(
            text = when (status) {
                LiveSessionStatus.Stopped -> "Demonstração parada"
                LiveSessionStatus.Running -> suggestion ?: "Ouvindo a demonstração"
                LiveSessionStatus.Ended -> "Demonstração encerrada"
            },
            style = MaterialTheme.typography.subtitle1,
            color = RockyColors.TextPrimary,
        )
        Text(
            text = "$messageCount mensagens simuladas · sem live real",
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.caption,
            color = RockyColors.TextSecondary,
        )
    }
}
