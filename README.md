# Rocky

Rocky is a local-first live-stream assistant for macOS and Windows. It follows the chat, finds recurring questions and useful signals, suggests what the streamer could say, and keeps notes and ideas from the session.

The project is built as a monorepo with Kotlin Multiplatform, Compose Multiplatform, SQLDelight, and Gradle.

> Rocky is currently an early desktop prototype. Installers are unsigned development builds and the real integration supports Twitch only.

[Leia em português](README.pt-BR.md).

[Website](https://opensources-com-br.github.io/rocky/) · [Alpha releases](https://github.com/opensources-com-br/rocky/releases)

## What works today

- movable, resizable, always-on-top, and compact desktop window;
- Twitch authentication through Device Code Flow, live chat through EventSub, and automatic reconnection;
- live Twitch viewer count and messages-per-minute metrics;
- grounded AI suggestions using local Ollama models, the OpenAI API, or OpenRouter;
- on-device speech synthesis through macOS and Windows system voices;
- wake-word voice commands with local `whisper.cpp` transcription;
- local SQLite notes with create, edit, delete, restore, and Markdown export;
- automated builds and development installers for macOS and Windows;
- English and Brazilian Portuguese interface, selected from the system language and adjustable in Settings.

Rocky does not require a Rocky account or a remote backend. The Twitch Client ID and AI provider settings are stored on the user's computer. Twitch access tokens remain in memory and are cleared when Rocky closes. Notes are stored in a local SQLite database. When a cloud AI provider is selected, the chat messages used as context are sent to that provider.

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

Run all automated checks with `./gradlew build` or `.\gradlew.bat build`.

## Configure a real session

1. Follow the [Twitch connection guide](docs/TWITCH.md) to register a public Twitch client and connect the streamer's own channel.
2. Follow the [AI provider guide](docs/AI.md) to use local Ollama, the OpenAI API, or OpenRouter.
3. Follow the [voice guide](docs/VOICE.md) to prepare local transcription and test an audio conversation.
4. Start a Twitch live stream, connect Rocky, and send a message from another account. New messages will appear in the Conversation tab.

The Twitch connector currently reads new chat messages and the current viewer count. Subscriptions, channel points, Super Chats, historical messages, and additional streaming platforms are not integrated yet.

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
| [apps/web](apps/web/) | Bilingual static landing page published with GitHub Pages |
| [shared/core](shared/core/) | Domain models, contracts, and export rules |
| [shared/data](shared/data/) | Twitch, AI, and SQLite implementations |
| [shared/ui](shared/ui/) | Compose UI and presentation state |
| [platform/desktop](platform/desktop/) | Native files, browser, preferences, audio, and transcription |
| [docs/adr](docs/adr/) | Architecture decision records |

The [implementation plan](docs/PLAN.md) describes the product direction, privacy model, architecture, delivery phases, and release criteria.

## License

Rocky is open-source software available under the [MIT License](LICENSE).

The launch candidate covers Twitch, AI, optional voice, grouped questions, notes, ideas, moments and a local records summary when disconnecting or closing the app. The summary includes saved pending questions; it does not cover the entire stream. YouTube, Super Chats and automatic idea generation remain outside this candidate. See [release preparation](docs/RELEASE_PREPARATION.md) and [data handling](docs/PRIVACY.md). Saved AI keys use system credential storage; text-only first use is supported.
