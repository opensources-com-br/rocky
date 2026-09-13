# Shared data

Implements core contracts for Twitch EventSub, Kick webhooks, YouTube live-chat polling, Ollama/OpenAI/OpenRouter, ElevenLabs streaming, updates and local SQLite records.

Depends on `shared/core`. SQLDelight schema and portable repository code live in `commonMain`; JVM HTTP, SQLite drivers and provider adapters live in `desktopMain`. Native audio playback and system services are injected through core contracts.

Run `./gradlew :shared:data:desktopTest` from the repository root. Simulated transports do not replace real account and webhook validation.
