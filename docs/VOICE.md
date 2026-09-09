# Voice setup

Rocky reads new suggestions with a voice installed on the computer. Speech synthesis stays on the device: macOS uses `say`, and Windows uses `System.Speech`. Open **Settings → Voice** to select a voice, adjust speed, test playback, or turn automatic reading off.

Rocky uses the system's default audio output. Windows also applies the volume selected in Rocky. On macOS, use the system output volume. A desktop-wide audio source in OBS may include Rocky's speech, so make a short test recording before going live.

## Local transcription

Streamer input is push-to-talk and uses [`whisper.cpp`](https://github.com/ggml-org/whisper.cpp) locally. Rocky does not download or bundle a model yet. Install `whisper.cpp` and download a multilingual GGML model such as `base`:

```shell
git clone https://github.com/ggml-org/whisper.cpp.git
cd whisper.cpp
sh ./models/download-ggml-model.sh base
cmake -B build
cmake --build build -j --config Release
```

On Windows, run the model download script from Git Bash, then build with a CMake environment that produces the Release executable.

Then open **Settings → Voice** and select:

- the `whisper-cli` executable (`build/bin/whisper-cli` on macOS, usually `build/bin/Release/whisper-cli.exe` on Windows);
- the downloaded model, for example `models/ggml-base.bin`;
- the microphone, or leave **Default input** selected.

During a real Twitch session, click **Talk to Rocky**, speak, and click it again. Rocky records at most 60 seconds, transcribes the command, shows it in the Conversation tab, and asks the configured language model to answer it from the received chat messages. The generated answer still has to cite real chat message IDs.

Microphone capture starts only after the click. The temporary 16-bit, 16 kHz mono WAV file is deleted after transcription, whether transcription succeeds or fails. Continuous listening and raw audio retention are not enabled.
