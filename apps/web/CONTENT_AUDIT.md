# Web content audit · 2026-09-23

Scope: landing page, 17 documentation topics, showcase coming-soon notice, disabled contribution action, preview labels and release copy. Authority: the current desktop source, cross-checked with the repository guides. This is a content audit, not launch certification or a hardware test.

| Topic | Implemented behavior used by the web | Source |
| --- | --- | --- |
| Platforms | Twitch, Kick, YouTube, Facebook and TikTok LIVE chat/viewers; Kick needs public HTTPS forwarding; TikTok uses unofficial WebCast through Eulerstream; no paid support or chat sending | `docs/TWITCH.md`, `docs/KICK.md`, `docs/YOUTUBE.md`, `docs/FACEBOOK.md`, `docs/TIKTOK.md` |
| AI | Ollama, OpenAI API, Anthropic API, Google Gemini API, xAI Grok API and OpenRouter; model discovery and bounded recent chat; loopback alone does not prove local inference | `docs/AI.md`, `shared/ui/.../AiSuggestionState.kt` |
| Conversation | Quick actions and voice; free-text field hidden; up to 30 answers and last 4 pairs for continuity | `shared/ui/.../AiSuggestionState.kt` |
| Profiles | On demand, discreet (5 min), proactive (2 min) | `shared/core/.../agent/InterventionProfile.kt` |
| Filters | Commands, configured bots, duplicate/burst rules over 30 seconds | `shared/core/.../live/ChatContextFilter.kt` |
| Voice | System/ElevenLabs TTS, local Whisper, calibration, buffered capture and bounded follow-ups | `docs/VOICE.md`, `shared/ui/.../VoiceState.kt` |
| Shortcuts | Ctrl+Shift+F8/F9/F10, configurable F keys | `shared/ui/.../ShortcutSettings.kt`, `docs/VOICE.md` |
| Records | User-saved notes/ideas/moments; detected questions persist | `shared/ui/.../VoiceCommand.kt`, `QuestionQueue.kt` |
| Summary | Deterministic summary of saved session records | `shared/core/.../live/RecordSummary.kt` |
| Storage | SQLite, system preferences, secure AI/ElevenLabs keys and Kick/YouTube Client Secrets | `docs/PRIVACY.md` |
| Maintenance | JSON backup/import, local diagnostic, verified installer download and update/restart in current packaged builds | `docs/PRIVACY.md`, `shared/ui/.../MaintenanceSettings.kt` |
| Multiple platforms | Conversation combines active chats; saved records share one stable Rocky session until every platform disconnects | `shared/ui/.../RockyWindow.kt`, `LiveWorkspace.kt` |
| Packaging | Gradle/Compose, macOS and Windows development installers | `README.md`, `apps/desktop/build.gradle.kts` |

`...` abbreviates `src/commonMain/kotlin/dev/rocky/{ui/window,core}` in the respective module.

Removed unsupported promises: Super Chat reading, automatic clip/idea detection, private OBS monitoring, Piper, generic compatible AI providers, TOML configuration, JavaScript plugins and Homebrew/Tauri installation instructions for Rocky.

Showcase examples remain in source but CASES is empty. Cases, filters and contribution form are inaccessible; Enviar o seu is disabled. Preserved examples are not current product documentation or user testimonials.

Earlier browser/layout results are historical. This audit compares documentation against the checked-out source; it does not certify native hardware, provider accounts, hosted content or release readiness. Current validation commands: `npm test`, `npm run lint`, `NEXT_PUBLIC_BASE_PATH=/rocky npm run build`.

When desktop capabilities change, update the relevant topic, landing copy and form options together. Do not present plans or design examples as implemented capabilities or measured user outcomes.
