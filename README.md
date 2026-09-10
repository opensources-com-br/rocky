# Rocky

Rocky is a local-first live-stream assistant for macOS and Windows. It follows the chat, finds recurring questions and useful signals, suggests what the streamer could say, and keeps notes and ideas from the session.

The project is built as a monorepo with Kotlin Multiplatform, Compose Multiplatform, SQLDelight, and Gradle.

> Rocky is currently an early desktop prototype. Installers are unsigned development builds and the real integration supports Twitch only.

[Leia em português](README.pt-BR.md).

[Website](https://opensources-com-br.github.io/rocky/) · [Latest alpha](https://github.com/opensources-com-br/rocky/releases/latest)

## What works today

- movable, resizable, always-on-top, and compact desktop window;
- clearly separated demonstration and real live sessions;
- Twitch authentication through Device Code Flow, live chat through EventSub, and automatic reconnection;
- grounded AI suggestions using local Ollama models or the OpenAI Responses API;
- on-device speech synthesis through macOS and Windows system voices;
- push-to-talk commands with local `whisper.cpp` transcription;
- local SQLite notes with create, edit, delete, restore, and Markdown export;
- Markdown export for the ideas shown during the demonstration;
- automated builds and development installers for macOS and Windows;
- English and Brazilian Portuguese interface, selected from the system language and adjustable in Settings.

Rocky does not require a Rocky account or a remote backend. Twitch access tokens and OpenAI API keys remain in memory. Notes are stored locally on the user's computer. When a cloud AI provider is selected, the chat messages used as context are sent to that provider.

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
2. Follow the [AI provider guide](docs/AI.md) to use a local Ollama model or an OpenAI API key.
3. Optionally follow the [voice guide](docs/VOICE.md) to select a system voice and configure local push-to-talk transcription.
4. Start a Twitch live stream, connect Rocky, and send a message from another account. New messages will appear in the Conversation tab.

The Twitch connector currently reads new chat messages only. Viewer counts, subscriptions, channel points, Super Chats, historical messages, and additional streaming platforms are not integrated yet.

## Build installers

```shell
./gradlew :apps:desktop:packageDmg
```

```powershell
.\gradlew.bat :apps:desktop:packageMsi :apps:desktop:packageExe
```

Packages are generated under `apps/desktop/build/compose/binaries/main/`. See the [desktop validation guide](docs/VALIDATION.md) for the complete build matrix and manual test checklist.

Tagged alpha versions publish the macOS DMG, Windows MSI and EXE, and SHA-256 checksums on [GitHub Releases](https://github.com/opensources-com-br/rocky/releases).

## Repository structure

| Directory | Responsibility |
| --- | --- |
| [apps/desktop](apps/desktop/) | Desktop entry point, packaging, and application lifecycle |
| [apps/web](apps/web/) | Bilingual static landing page published with GitHub Pages |
| [shared/core](shared/core/) | Domain models, contracts, and export rules |
| [shared/data](shared/data/) | Twitch, AI, and SQLite implementations |
| [shared/ui](shared/ui/) | Compose UI and presentation state |
| [platform/desktop](platform/desktop/) | Native files, browser, preferences, audio, and transcription |
| [fixtures](fixtures/) | Synthetic demonstration and test inputs |
| [docs/adr](docs/adr/) | Architecture decision records |

The [implementation plan](docs/PLAN.md) describes the product direction, privacy model, architecture, delivery phases, and release criteria.

## License

Rocky is open-source software available under the [MIT License](LICENSE).
