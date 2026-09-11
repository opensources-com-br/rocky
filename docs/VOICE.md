# Voice setup

Rocky uses a voice installed on the computer to answer. macOS uses `say`, and Windows uses `System.Speech`. In **Settings → Voice**, select a voice, adjust its speed, and run the playback test.

## Streamer recognition

The microphone stays active during a real session. Rocky processes audio locally in short segments and only runs a command after recognizing its name. Say, for example, **“Rocky, what does chat want to play?”**. The recognized text appears in Conversation, Rocky checks messages from the last two minutes, and answers aloud.

On macOS with Homebrew, click **Set up voice recognition**. Rocky installs `whisper.cpp`, downloads the multilingual model, and saves both paths on the device. On Windows, manually select the `whisper-cli` executable, GGML model, and microphone.

Use **Test voice conversation** before going live. The panel shows the transcript and plays a response to validate the microphone, Whisper, and audio output together. During a session, the footer shows the captured input level; the header distinguishes `CHAT ACTIVE` from `MIC ON`.

The temporary WAV is 16-bit, 16 kHz mono and is deleted after each transcription, including failures. Raw audio is neither sent to the AI provider nor retained by Rocky.
