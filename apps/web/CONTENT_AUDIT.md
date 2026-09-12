# Web content audit · 2026-09-12

Scope: landing page, 17 documentation topics, 9 showcase scenarios, contribution form, preview labels and release copy. Authority: the current desktop source, cross-checked with the repository guides. This is a content audit, not launch certification or a hardware test.

| Topic | Implemented behavior used by the web | Source |
| --- | --- | --- |
| Platforms | Twitch and Kick chat/viewers; Kick needs public HTTPS forwarding; no paid support or chat sending | `docs/TWITCH.md`, `docs/KICK.md` |
| AI | Ollama, OpenAI API, OpenRouter; bounded recent chat | `docs/AI.md`, `shared/ui/.../AiSuggestionState.kt` |
| Conversation | Up to 30 session answers; last 4 pairs for continuity | `shared/ui/.../AiSuggestionState.kt` |
| Profiles | On demand, discreet (5 min), proactive (2 min) | `shared/core/.../agent/InterventionProfile.kt` |
| Filters | Commands, configured bots, duplicate/burst rules over 30 seconds | `shared/core/.../live/ChatContextFilter.kt` |
| Voice | System TTS, local whisper.cpp, optional endpoint detection | `docs/VOICE.md`, `shared/ui/.../VoiceState.kt` |
| Shortcuts | Ctrl+Shift+F8/F9/F10, configurable F keys | `shared/ui/.../ShortcutSettings.kt`, `docs/VOICE.md` |
| Records | User-saved notes/ideas/moments; detected questions persist | `shared/ui/.../VoiceCommand.kt`, `QuestionQueue.kt` |
| Summary | Deterministic summary of saved session records | `shared/core/.../live/RecordSummary.kt` |
| Storage | SQLite, system preferences, secure AI and Kick credentials | `docs/PRIVACY.md` |
| Maintenance | JSON backup/import, local diagnostic, update link | `docs/PRIVACY.md`, `shared/ui/.../MaintenanceSettings.kt` |
| Packaging | Gradle/Compose, macOS and Windows development installers | `README.md`, `apps/desktop/build.gradle.kts` |

`...` abbreviates `src/commonMain/kotlin/dev/rocky/{ui/window,core}` in the respective module.

Removed unsupported promises: simultaneous multi-platform ingestion, Super Chat reading, automatic clip/idea detection, private OBS monitoring, Piper/ElevenLabs, generic compatible AI providers, TOML configuration, JavaScript plugins and Homebrew/Tauri installation instructions for Rocky.

Showcase identities, channel links, performance numbers and testimonial claims from the visual references were replaced with explicitly illustrative scenarios. The contribution form offers current integrations and only generates a local draft; publication requires review.

Validation: ESLint; both existing Markdown formatter tests; static build with `/rocky` base path; all documentation topics and scenario details; category filtering; supported form options; desktop/mobile routes without horizontal overflow or browser errors.

When desktop capabilities change, update the relevant topic, landing copy and form options together. Do not present plans or design examples as implemented capabilities or measured user outcomes.
