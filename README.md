# Rocky

Rocky is a local-first live-stream assistant for macOS and Windows. It follows the chat, finds recurring questions and useful signals, suggests what the streamer could say, and keeps notes and ideas from the session.

The project is built as a monorepo with Kotlin Multiplatform, Compose Multiplatform, SQLDelight, and Gradle.

> Rocky is currently an early desktop prototype. Real chat integration is available for Twitch, Kick, YouTube, Facebook, and TikTok LIVE; Kick requires a public HTTPS webhook forwarder, while TikTok uses an unofficial WebCast connection.

[Leia em português](README.pt-BR.md).

[Website](https://opensources-com-br.github.io/rocky/) · [Alpha releases](https://github.com/opensources-com-br/rocky/releases)

## What works today

- movable, resizable, always-on-top, and compact desktop window;
- Twitch authentication through Device Code Flow, live chat through EventSub, and automatic reconnection;
- Kick OAuth, signed chat webhooks, and event subscriptions;
- YouTube desktop OAuth, active-broadcast discovery, and live-chat polling;
- TikTok LIVE chat through a desktop connection by public username;
- live viewer count and messages-per-minute metrics for Twitch, Kick, YouTube, Facebook, and TikTok;
- grounded AI suggestions using local Ollama models, the OpenAI API, or OpenRouter;
- speech through macOS/Windows system voices or ElevenLabs, with your own key, streaming and optional local fallback;
- wake-word voice commands with local `whisper.cpp` transcription;
- local SQLite notes with create, edit, delete, restore, and Markdown export;
- automated builds and development installers for macOS and Windows;
- English and Brazilian Portuguese interface, selected from the system language and adjustable in Settings.

Rocky does not require a Rocky account or bundled remote backend. Platform and AI settings are stored on the user's computer. Kick and YouTube Client Secrets and saved AI/ElevenLabs keys use the system credential vault; platform access tokens remain in memory. Notes are stored in a local SQLite database. When a cloud AI provider is selected, the chat messages used as context are sent to that provider. When selected, ElevenLabs receives the text to be spoken.

## Run from source

Requirements:

- macOS or Windows;
- JDK 17;
- Git.

Clone the repository and launch the desktop app:

```shell
git clone https://github.com/opensources-com-br/rocky.git
cd rocky
./gradlew :apps:desktop:run
```

On Windows PowerShell:

```powershell
git clone https://github.com/opensources-com-br/rocky.git
cd rocky
.\gradlew.bat :apps:desktop:run
```

Run the Kotlin checks with `./gradlew build` or `.\gradlew.bat build`.

Validate the web separately: in `apps/web`, run `npm ci`, `npm test`, `npm run lint` and `npm run build` (Node.js 22, as used in CI).

## Configure a real session

1. Follow the [Twitch](docs/TWITCH.md), [Kick](docs/KICK.md), [YouTube](docs/YOUTUBE.md), [Facebook](docs/FACEBOOK.md), or [TikTok](docs/TIKTOK.md) connection guide. Kick requires a public HTTPS endpoint that forwards signed webhooks to Rocky.
2. Follow the [AI provider guide](docs/AI.md) to use local Ollama, the OpenAI API, or OpenRouter.
3. Follow the [voice guide](docs/VOICE.md) to prepare local transcription and test an audio conversation.
4. Start a live stream, connect Rocky, and send a message from another account. New messages will appear in the Conversation tab.

The Twitch, Kick, YouTube, Facebook, and TikTok connectors read new chat messages and the current viewer count. Paid support events, channel points, Stars, Super Chats, gifts, and historical messages are not integrated yet. Facebook requires a Business app with `pages_show_list`, `pages_read_engagement`, and `pages_read_user_content`, plus an active live on an authorized Page. TikTok relies on the unofficial WebCast protocol and Eulerstream connection service.

## Build installers

```shell
./gradlew :apps:desktop:packageDmg
```

```powershell
.\gradlew.bat :apps:desktop:packageMsi :apps:desktop:packageExe
```

Packages are generated under `apps/desktop/build/compose/binaries/main/`. See the [desktop validation guide](docs/VALIDATION.md) for the complete build matrix and manual test checklist.

Tagged alpha versions publish the macOS DMG, Windows MSI and EXE, and SHA-256 checksums on [GitHub Releases](https://github.com/opensources-com-br/rocky/releases).

Before promoting an alpha, follow the [streamer test protocol](docs/STREAMER_TEST.md) for clean installation, a prolonged live session, OBS, and connection recovery.

## Repository structure

| Directory | Responsibility |
| --- | --- |
| [apps/desktop](apps/desktop/) | Desktop entry point, packaging, and application lifecycle |
| [apps/web](apps/web/) | Rocky landing, documentation and showcase in Next.js |
| [shared/core](shared/core/) | Domain models, contracts, and export rules |
| [shared/data](shared/data/) | Twitch, Kick, YouTube, Facebook, TikTok, AI, and SQLite implementations |
| [shared/ui](shared/ui/) | Compose UI and presentation state |
| [platform/desktop](platform/desktop/) | Native files, browser, preferences, audio, and transcription |
| [docs/adr](docs/adr/) | Architecture decision records |

The [implementation plan](docs/PLAN.md) describes the product direction, privacy model, architecture, delivery phases, and release criteria.

## License

Rocky is open-source software available under the [MIT License](LICENSE).

The current code covers Twitch, Kick, YouTube, Facebook, TikTok, AI, optional voice, grouped questions, notes, ideas, moments and a local records summary when disconnecting or closing the app. Kick, YouTube, Facebook, and TikTok still need validation in real live sessions before release; Kick also needs a public webhook. Super Chats, Stars, gifts, and automatic idea generation remain outside this candidate. See [release preparation](docs/RELEASE_PREPARATION.md) and [data handling](docs/PRIVACY.md).
