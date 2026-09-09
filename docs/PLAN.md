# Rocky — product and implementation plan

Status: proposal to begin implementation. Date: September 8, 2026.

Read in [Português (Brasil)](PLAN.pt-BR.md).

Rocky is a live-stream assistant for macOS and Windows: a floating window that follows chat, speaks or writes to the streamer, organizes paid support events, and captures useful notes and ideas. It is a free, open-source application with processing and storage controlled by the user, no Rocky account, and no required backend.

The supplied screenshots are visual and behavioural references. They do not guarantee a particular integration, model, voice, metric, or platform capability.

## 1. Decisions to start with

| Area | Proposal |
| --- | --- |
| Technology | Kotlin Multiplatform (KMP) for shared logic and Compose Multiplatform for UI |
| Desktop | JVM target with bundled runtime and isolated native integrations |
| Initial systems | macOS Apple Silicon and Windows x64; validate Intel Macs before announcing support |
| Rocky account | None; access is protected by the operating-system account |
| Data | Local SQLite; credentials in the system credential store |
| Initial AI | Local Ollama and user-provided OpenAI API key |
| Later providers | Claude, Gemini, Grok, and compatible endpoints behind provider adapters |
| First live chat | Twitch, then YouTube with Super Chat validation |
| Voice | Push-to-talk/shortcut, local transcription, and local synthesis in the MVP |
| Distribution | Direct download first; Mac App Store and Microsoft Store later |
| Web | Static landing page and documentation in this monorepo |
| License | MIT proposed for project code; verify distributed component licenses |

KMM was the mobile-focused name; KMP better describes this project. Compose Desktop can create installers that include the Java runtime so users do not install a JDK separately. Build and test packages on their target operating systems. [Kotlin documentation](https://kotlinlang.org/docs/multiplatform/compose-native-distribution.html).

The desktop stores are the Mac App Store and Microsoft Store; Google Play is an Android phase. Direct distribution lets the project validate the product before taking on store requirements. Apple requires sandboxing for Mac App Store submissions and supports Developer ID/notarization for direct distribution. Microsoft accepts MSI/EXE packages subject to its requirements. [Apple](https://developer.apple.com/macos/distribution/), [Microsoft](https://learn.microsoft.com/en-us/windows/apps/publish/publish-your-app/msi/app-package-requirements).

## 2. User journey

1. Install and open Rocky without creating an account.
2. Try a simulated live session with no credentials, model download, or microphone.
3. Choose local processing or cloud AI; the UI says where data goes and whether a third party may charge for usage.
4. In local mode, connect an installed Ollama instance and select an available model. Any model download is explicit and displays its size and license.
5. In cloud mode, enter an API key, choose a model, and test the connection. The key never appears in history.
6. Authorize Twitch or YouTube in the provider's official browser flow and choose a channel/live stream.
7. Select microphone and output, test voice, and configure a shortcut.
8. Start a session, then type or hold the talk control to ask Rocky.
9. End the session and review/export its summary, open questions, notes, and ideas as Markdown or JSON.

Primary example: a streamer asks their audience which topic they want next, then asks Rocky to summarize the last two minutes. Rocky groups answers, explains the most common options, and makes the source messages inspectable. It says “among the messages received”, never claiming to represent every viewer.

Continuous listening is a later opt-in mode. With push-to-talk alone, Rocky does not know everything said during a live stream. A note about a spoken promise must come from authorized transcription, an explicit command, or manual input; ideas inferred solely from chat must say so.

## 3. Local operation, privacy, and security

Rocky does not need a proprietary login/password in the first release. That would add account recovery and infrastructure without product value. Opening Rocky, authorizing a live-stream platform, and connecting an AI provider are separate actions.

| Mode | Stays on the computer | Uses the network |
| --- | --- | --- |
| Local AI | Inference, transcription, synthesis, notes, and context | Live chat and user-selected downloads |
| Cloud AI | App, rules, storage, and context selection | The minimum chat/context sent to the selected provider, plus live chat |
| Offline demo | Fixtures and simulated interaction | Nothing |

The product wording should be “local data and AI you choose.” Claim fully local AI only when language model, transcription, and voice are truly local. A live chat needs internet. A localhost Ollama endpoint alone does not prove local inference because Ollama can also reach cloud models; use local models and test with the network disabled. [Ollama API](https://docs.ollama.com/api/introduction).

Implementation requirements:

- Use a `SecretStore`: Keychain on macOS and Credential Manager/DPAPI on Windows. Do not place API keys in SQLite, preferences, logs, exports, or source control.
- User-supplied keys belong to the user. Never ship a project AI key with the app. A credential store protects persisted credentials; it cannot secure a compromised computer.
- Authenticate platform accounts in the browser with least-privilege scopes. Never ask for Twitch, Google, or AI-provider passwords.
- For Google desktop, use an installed-app flow with PKCE, state, and temporary loopback callback. A client secret bundled in a desktop app is not a secret. [Google OAuth](https://developers.google.com/identity/protocols/oauth2/native-app).
- For Twitch, use Device Code Flow for a public client, with refresh, validation, and revocation. [Twitch OAuth](https://dev.twitch.tv/docs/authentication/getting-tokens-oauth/).
- Do not expose a listener to the local network. Bind local endpoints to loopback only; require TLS and explicit configuration for remote endpoints.
- Treat chat as untrusted data. A chat message cannot change Rocky rules or access credentials, shell commands, automatic browsing, or public posting.
- Allow Rocky to produce responses and suggest notes/ideas. Validate structured output before speaking or rendering it. Streamer commands travel through a separate channel from chat.
- Filter support messages, cap their length, block spoken URLs, and require manual approval by default. Payment cannot bypass safety controls.
- Request microphone access only when needed, show a real capture indicator, provide an immediate stop control, and discard raw audio after processing by default.
- Keep chat/transcription temporarily during the session. Preserve only selected notes and summaries after it ends by default; make full-history retention opt-in, time-limited, visible, and deletable.
- Persist the support queue during a session for crash recovery, then clear it according to retention settings. Keep rotated technical logs without sensitive content; disable telemetry by default.
- Provide session export and deletion. SQLite is not encrypted by default, so do not make a false encryption claim for persisted history.
- Never silently fall back from local AI to cloud AI when a local model fails.

## 4. AI providers and accounts

Do not promise that a ChatGPT, Claude, Gemini, or Grok application subscription can be used by Rocky. The viable MVP promise is **bring your own API key or use a local model**. Subscription login integration only belongs in Rocky when there is an official mechanism that applies to this product; do not reuse cookies or credentials from other clients.

OpenAI documents application requests using API credentials; that does not establish a universal ChatGPT sign-in for Rocky. [OpenAI API authentication](https://developers.openai.com/api/reference/overview).

| Adapter | Configuration | Phase |
| --- | --- | --- |
| Ollama | Local endpoint and available local model; capability test | MVP |
| OpenAI | User API key and model through Responses API | MVP |
| Claude | User key and its official API | After MVP |
| Gemini | Currently supported Google AI Studio credential and official API | After MVP |
| Grok | User key and official provider API | After MVP |
| OpenAI-compatible | Base URL, optional key, and tested capabilities | After MVP |

Do not force Claude and Gemini through an OpenAI-shaped API. Each adapter owns streaming, cancellation, limits, error handling, and structured-output translation for its provider. [Claude](https://platform.claude.com/docs/en/api/overview), [Gemini](https://ai.google.dev/gemini-api/docs/api-key), [Grok](https://docs.x.ai/overview).

Language model, transcription, and speech synthesis are separate selections. Expose only controls that the selected model supports. A generic “creativity” slider must not alter factual counts.

Control costs by batching events, deduplicating messages, limiting context and output, caching analyses, and running at most one automatic analysis at a time. Do not make one AI request per chat message. Show estimated tokens/cost where available, a session budget, and a stop button; local app limits do not guarantee a provider's final invoice.

## 5. Live-stream platforms

| Platform | Technical path | Initial commitment |
| --- | --- | --- |
| Twitch | EventSub WebSocket and public OAuth | First real connector; chat and reconnection |
| YouTube | Live Streaming API `liveChatMessages.streamList`; quota-aware fallback | Chat and Super Chats in the MVP |
| Kick | Documented webhook events | Later spike; do not promise simple local-only connection |
| Facebook | APIs and permissions not validated here | Out of MVP; do not display as available |

EventSub keeps a connection in the desktop client. Handle keepalives, reconnection, subscription recreation when needed, and event deduplication. A disconnect can lose messages, so an incomplete interval must never be displayed as complete history. [Twitch WebSocket](https://dev.twitch.tv/docs/eventsub/handling-websocket-events/).

YouTube provides dedicated Super Chat and Super Sticker message types. Validate authentication, quota, live discovery, and stream-end handling before marking the connector complete. [Messages](https://developers.google.com/youtube/v3/live/docs/liveChatMessages), [streamList](https://developers.google.com/youtube/v3/live/docs/liveChatMessages/streamList).

Kick's documented events require a receiver reachable from its service. This requires an architectural choice: wait for a suitable official alternative, offer an optional self-hosted relay, or add opt-in infrastructure later. A relay is not an MVP requirement. Do not scrape or rely on internal protocols. [Kick webhooks](https://github.com/KickEngineering/KickDevDocs/blob/main/events/webhook-security.md).

Use **Support** as the aggregate tab and **Super Chat** only for YouTube's type. Preserve original support type and unit instead of converting Bits or other platform events into a fictitious currency equivalence. Store monetary values as integer provider units plus currency code, never floats.

OAuth application registration, possible consent verification, quotas, and test accounts are release dependencies, not Rocky account registration. Contributor builds can configure their own client ID. Simulated tests must not require secrets.

## 6. Assistant behaviour

Pipeline: connectors → normalized events → deduplication/moderation → bounded context → question clustering → evidence selection → LLM → validation → intervention policy → text/voice/notes.

- `ChatEvent`: platform, channel, external ID, author, source/received timestamps, text, type, and support/moderation fields.
- `LiveSession`: start/end, connected channels, and capture-failure intervals.
- `TopicCluster`: topic, source messages, total messages, and distinct authors per platform.
- `Intervention`: evidence, priority, created/expiry timestamps, and state.
- `SupportEvent`: stable ID, currency/unit, and pending/playing/read/skipped state.
- `Note` and `Idea`: session timestamp, manual/AI origin, evidence, and review state.
- `ProviderProfile`: provider, model, capabilities, and credential reference—never the key itself.

Deterministic rules decide frequency and timing; the LLM synthesizes content. Counts come from events, not model output. “Seven people” requires seven distinct authors in the known scope. Across platforms, identities cannot be deduplicated without an explicit identity link; prefer “seven messages” or “seven accounts”.

Initial policy: on-demand mode by default; proactive mode is opt-in, limited to three interventions per ten minutes with a cooldown and short answers. Priority order: direct streamer command, selected support message, repeated relevant question, idea. Ideas cannot indefinitely block support events.

States are idle, capturing command, analysing, speaking, paused, and degraded. “Listening to chat” and “microphone active” are distinct indicators. Silence stops audio; session pause stops collection/analysis according to its visible setting.

Use a single speech queue, immediate cancellation, and expiry for stale ideas. Do not silently drop paid support due to a full queue. Persist support state to avoid replay after reconnection; after a crash during speech, mark delivery uncertain and ask whether to replay instead of claiming exactly-once delivery.

For later continuous listening: use local voice activity detection, initially wait for three seconds of silence, suspend intervention while the streamer speaks, and suppress Rocky's own voice in recognition. Push-to-talk makes the MVP useful without this dependency.

Post-live summaries use incremental notes and summaries with evidence while respecting retention. “Entire live” means summarized/retrieved context, not sending every message to the model each time. Clip entries are timestamp suggestions; the MVP does not record or cut video.

## 7. Window and visual direction

Keep the screenshots' dark surface, orange accent, prominent spoken text, cards, and tabs. Real typography needs redistribution rights and readable fallbacks. Screenshot pixel dimensions are not a responsive layout specification.

- Proposed expanded size: about 420 × 680 dp; compact size: about 340 × 180 dp. Validate on real displays.
- Drag from header; support resize, minimize, always-on-top, and restore from tray/menu bar.
- Persist window position and topmost state separately; recover the window if a monitor disappears.
- Do not steal focus on incoming chat. Respect native shortcuts and UI conventions; do not mimic macOS window controls on Windows.
- Test multiple monitors, DPI, fullscreen, and Spaces. Always-on-top cannot be guaranteed above exclusive fullscreen games.
- Tabs: Conversation, Support, Notes, Ideas, and Pulse. Settings: Agent, AI, Voice, Platforms, and Data & Privacy.
- Provide empty/error states for disconnected, no live stream, unavailable model, quota exhausted, microphone permission denied, and reconnecting.
- Pulse displays locally computed messages/minute; viewer count only when supplied by an API and stamped with its measurement time. A cross-platform sum is not a unique audience. Missing data is unavailable, not zero.
- Saving a note keeps timestamp and evidence. Next explicitly dismisses the current intervention. Silence is visible and reversible.
- Make controls keyboard-accessible, support text scaling, and pair platform colour with a name.

Replace “only you hear it; it never enters the broadcast” with “monitoring output” and an OBS test flow. Desktop-audio capture can broadcast Rocky's voice. Let the user select output where supported and document per-system routing. [OBS guide](https://obsproject.com/kb/application-audio-capture-guide).

## 8. Architecture and monorepo

Start with few Gradle modules. Keep connectors and providers as internal packages until separate build/release boundaries are justified.

```text
rocky/
  apps/
    desktop/               # JVM entry point, window, dependency composition
    web/                   # static landing page and documentation
  shared/
    core/                  # domain, contracts, context, intervention policy
    data/                  # connectors, providers, SQLite, repositories
    ui/                    # Compose components and presentation state
  platform/
    desktop/               # credential store, audio, shortcuts, tray, native APIs
  docs/
    PLAN.md
    PLAN.pt-BR.md
    adr/                   # architecture decisions as they are implemented
  fixtures/                # synthetic events and replay scenarios
  .github/workflows/
  gradle/libs.versions.toml
  settings.gradle.kts
  build.gradle.kts
  README.md
  README.pt-BR.md
  CONTRIBUTING.md
  SECURITY.md
  LICENSE
```

Keep shared domain code in `commonMain`, without AWT, `java.io`, or desktop-only dependencies. Place desktop implementations in `jvmMain` or the platform module. Define contracts such as `ChatConnector`, `LlmProvider`, `SpeechRecognizer`, `SpeechSynthesizer`, `AudioOutput`, `SecretStore`, `SessionRepository`, and `WindowController`.

Proposed stack: Coroutines/Flow, kotlinx.serialization, Ktor Client, and SQLite through SQLDelight, subject to version compatibility checks at bootstrap. Start with constructor injection and immutable `StateFlow` UI state. Pin toolchain/dependency versions and lock files; avoid dynamic versions.

Candidate transcription architecture: run whisper.cpp as a constrained local helper process. Test Portuguese quality, cancellation, resource use, and packaging. Use local system voices first behind an adapter; validate rendering and output choice. Available voices vary by computer, so names in screenshots are not guaranteed assets. [whisper.cpp](https://github.com/ggml-org/whisper.cpp).

Keep the local AI runtime external at first through Ollama. Do not bundle large models or silently install them. Consider an integrated model manager later only if it materially improves the experience.

For web, use Astro plus Markdown/MDX for landing, installation, providers, privacy, audio/OBS, contribution, and changelog pages. Share brand/tokens, but do not require the desktop UI and web UI to use the same framework. A landing page must not require a backend, user login, or user API keys. [Astro content collections](https://docs.astro.build/en/guides/content-collections/).

Mobile comes later. Sharing domain/connectors and parts of the UI does not mean copying the desktop window. First consider a companion app for notes and session control; autonomous operation and background behaviour require platform-specific validation on iOS and Android. Do not add network pairing in the first release.

## 9. Milestones and completion criteria

Estimates are directional for one experienced full-time contributor, excluding provider/store review waits. Re-estimate after spikes; these are not guaranteed deadlines.

| Stage | Estimate | Deliverable and definition of done |
| --- | --- | --- |
| 0 — Feasibility | 2–4 days | Window on both systems; credential store, audio, OAuth, and chat-receipt proofs; documented risks |
| 1 — Usable foundation | 3–5 days | Monorepo, compact/expanded window, tabs, fixtures, note save/export, macOS and Windows builds |
| 2 — First real flow | 4–7 days | Twitch → context → Ollama/OpenAI → textual response with evidence; reconnection and limits |
| 3 — Voice and YouTube | 5–8 days | Push-to-talk, local STT/TTS, output selection/test, YouTube, and Super Chat queue |
| 4 — Public alpha | 4–7 days | Summary, tested installers, OBS guide, landing/docs, retention and security checks |

Initial total: roughly 18–31 working days, with the main uncertainty in native integrations, audio, and external approvals. The first visual, usable result arrives in Stage 1 rather than waiting for the complete alpha.

Initial backlog:

1. Bootstrap Gradle Wrapper, JDK, version catalogue, and macOS/Windows CI; prove minimal builds on both.
2. Create the domain, injectable clock, contracts, simulated connector, and Portuguese fixtures.
3. Implement floating/compact window, position persistence, tray, and topmost behaviour.
4. Build the screenshot-inspired components and real connection/capture states.
5. Implement `SecretStore` and settings without credential leakage.
6. Register/configure public Twitch client, Device Code Flow, and EventSub.
7. Build the bounded pipeline, verifiable counts, sources, and reconnection.
8. Add Ollama and OpenAI with connection test, timeout, and cancellation.
9. Add SQLite notes/export and retention.
10. Add local audio, shortcut, and immediate speech cancellation.
11. Add YouTube and persistent support-queue states.
12. Prepare installers, public documentation, and the first alpha.

Out of MVP: Kick/Facebook, automatic public chat posting, moderation with ban power, screen capture, video recording, automatic clipping, continuous listening, voice cloning, sync, executable plugins, and mobile apps.

## 10. Validation and release

Essential tests: deduplication/reconnection; per-author counting; high chat pressure with bounded queues; repeated support events; suggestion expiry; voice interruption; missing evidence; chat prompt injection; timeout/429; invalid schemas; history deletion; credential removal.

Use synthetic replay scenarios for quality: repeated questions, sarcasm, disagreement, spam, bursts of support events, and empty chat. Evaluate relevance, count fidelity, and unwanted interruptions. Default tests require neither a real live stream nor keys.

Proposed benchmark targets, not measured results: speech stop p95 under 300 ms; text response p95 under 5 s after a command reaches Rocky on a reference profile; a two-hour session at 100 messages/s with bounded memory and no UI freeze. Measure CPU/GPU and OBS impact; publish the hardware/model profile and separate app memory from AI memory. Revisit targets after Stage 0.

Alpha gate: a person can install on a clean machine, connect a live stream, ask by text/voice, receive a synthesis backed by source messages, read a Super Chat without duplication after reconnect, save notes, and export/end the session. Local mode processes fixtures without network access. Verify Rocky's audio is excluded from the broadcast using an OBS test recording.

CI compiles/tests both systems. Release creates architecture-specific artifacts, checksums, and signing/notarization appropriate to the channel. Protect signing secrets, publish build instructions and a dependency/license inventory. In alpha, notify users of updates and link to official download; never install an update during a live stream. Automatic updates require a separate verification/rollback design.

MIT is the proposed project-code license, not yet applied by this plan. Add contribution, vulnerability reporting, and data policy documents. Models, voices, and graphics need compatible licenses before redistribution. Rocky needs its own visual/voice identity; the fiction reference inspires the concept but does not establish rights to a name, domain, or official material.

External items to resolve during implementation: owner accounts for OAuth/publishing, test credentials, signing certificates, store-name availability, and Windows test hardware. None block local bootstrap with fixtures.
