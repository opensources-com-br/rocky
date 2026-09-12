# Voice setup

Rocky uses a voice installed on the computer to answer. macOS uses `say`, and Windows uses `System.Speech`. In **Settings → Voice**, select a voice, adjust its speed, and run the playback test.

## Streamer recognition

Voice is optional. After it is configured, listening starts when Twitch connects; the footer turns listening on or off. Rocky processes audio locally in short segments and only runs a command after recognizing its name. Say, for example, **“Rocky, what does chat want to play?”**. The recognized text appears in Conversation, Rocky checks up to 200 received messages from the last two minutes, and answers aloud.

On macOS with Homebrew, click **Set up voice recognition**. Rocky installs `whisper.cpp`, downloads the multilingual model, and saves both paths on the device. On Windows, manually select the `whisper-cli` executable, GGML model, and microphone.

Use **Test voice conversation** before going live. The panel shows the transcript and plays a response to validate the microphone, Whisper, and audio output together. During a session, the footer shows the captured input level; the header distinguishes `CHAT ACTIVE` from `MIC ON`.

The temporary WAV is 16-bit, 16 kHz mono and is deleted after each transcription, including failures. Raw audio is neither sent to the AI provider nor retained by Rocky.

Each capture lasts eight seconds. Speak while the input meter is active; capture pauses during transcription and replies. Silencing or skipping an answer resumes listening when listening is enabled. The configured agent name is also the wake word. The interface language selects the response/transcription language; choose an installed system voice for that language.

Managed downloads are pinned to a repository revision and checked against SHA-256. Setup can be cancelled and retried. Windows still requires an external whisper-cli/model; macOS automatic setup requires Homebrew. The volume control is disabled on macOS because system output volume must be used.

Use headphones and record an OBS test: desktop audio capture can include Rocky. This build does not provide a separate output-device selector or guarantee private monitoring.
