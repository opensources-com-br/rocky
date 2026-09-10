# Streamer alpha test

This protocol validates Rocky in the conditions that matter during a real stream. The English version is canonical; see [STREAMER_TEST.pt-BR.md](STREAMER_TEST.pt-BR.md) for Portuguese.

Use the latest prerelease from [GitHub Releases](https://github.com/opensources-com-br/rocky/releases). Record every run with the template at the end of this document. A check counts as passed only when its evidence is recorded.

## Release gate

Before promoting an alpha build, complete:

- one clean installation on a supported macOS version;
- one clean installation on Windows 11;
- at least three real Twitch sessions with two different streamers;
- one continuous session lasting at least two hours;
- one OBS recording on each operating system;
- every network interruption scenario below.

Crashes, lost notes, a session mixing messages from a previous channel, unrecoverable Twitch connections, and Rocky audio unexpectedly entering the broadcast are release blockers.

## 1. Clean installation

Use a virtual-machine snapshot, a new operating-system user, or a computer where Rocky has never run. Do not install a separate JDK.

1. Download the installer and `SHA256SUMS.txt` from the same release.
2. Verify the checksum:

   ```shell
   shasum -a 256 Rocky-*.dmg
   ```

   ```powershell
   Get-FileHash .\Rocky-*.msi -Algorithm SHA256
   Get-FileHash .\Rocky-*.exe -Algorithm SHA256
   ```

3. Install and launch Rocky from Finder or the Windows Start menu.
4. Confirm the first-use guide opens and follows the operating-system language.
5. Switch between English and Portuguese, close Rocky, reopen it, and confirm the choice persists.
6. Complete the required Twitch and AI checks. Confirm **Finish setup** becomes available without testing voice; then configure and test voice optionally.
7. Close and reopen Rocky. Confirm the guide stays completed and local notes remain available.
8. Uninstall Rocky through Finder or Windows Installed apps. Confirm no launcher shortcut remains.

Record operating-system security warnings. Alpha packages are currently unsigned, so warnings are expected; a silent failure or an app that never opens is not.

## 2. Prolonged live session

Run a real Twitch stream for at least two hours. Use a second Twitch account to send chat messages.

- Send normal messages continuously and a burst of at least 20 messages within one minute.
- Generate AI suggestions manually and automatically throughout the session.
- Save, edit, delete, and export notes after at least 30, 60, and 120 minutes.
- Test voice playback at least once every 30 minutes.
- If `whisper.cpp` is configured, record and transcribe at least five streamer commands.
- Switch between the conversation, notes, ideas, and pulse tabs.
- Resize, pin, minimize, and use compact mode while OBS is running.
- Record Rocky's memory at launch, after 30 minutes, after 60 minutes, and at the end.

Pass criteria:

- the interface remains responsive and no restart is required;
- new chat messages continue to arrive in order;
- only the latest 1,000 messages remain in memory;
- memory does not grow continuously after the first 30 minutes or increase by more than 300 MB;
- saved notes survive an app restart and exported Markdown opens correctly;
- voice and transcription operations end or cancel without leaving the controls busy.

## 3. OBS validation

Create a short local recording before the public stream.

1. Add Rocky through Window Capture and confirm the full and compact layouts render without flicker.
2. Pin Rocky above OBS and another application, then unpin it and confirm normal window ordering returns.
3. Capture only the streamer's microphone. Trigger **Test voice** and confirm Rocky is audible to the streamer but absent from the recording.
4. Add desktop audio capture intentionally. Confirm Rocky becomes audible in the recording and document that choice.
5. Play a Rocky suggestion while the streamer speaks. Confirm it does not create echo, feedback, or repeated transcription.
6. Start and stop push-to-talk while OBS records. Confirm only the requested microphone is captured by Rocky.
7. Review the recording for clipping, doubled audio, missing microphone audio, and accidental exposure of settings or API keys.

Use headphones during voice tests. Do not display the AI settings screen on a public scene while an API key is present.

## 4. Connection interruption

Keep a second account ready to send a unique message after each recovery.

1. Disable the network for 15 seconds, restore it, and send `recovery-15`.
2. Disable the network for 60 seconds, restore it, and send `recovery-60`.
3. Switch between Wi-Fi and Ethernet or another network, then send `recovery-switch`.
4. Disconnect Twitch from Rocky while a reconnect is pending. Wait 45 seconds and confirm it does not reconnect by itself.
5. Connect again, then close and reopen Rocky. Confirm a new authorization is requested because tokens are memory-only.

Pass criteria:

- Rocky shows a reconnecting state after connectivity is lost;
- chat resumes within 60 seconds after the network becomes usable;
- each recovery message appears once;
- messages received before the interruption remain visible until displaced by the 1,000-message limit;
- disconnecting cancels pending recovery and no old session events enter the new session;
- a failed recovery produces a visible error and allows a new connection attempt.

## Test record

```text
Date and duration:
Tester / streamer:
Rocky version and commit:
Installer and SHA-256:
Operating system, version, and architecture:
Display resolution and scaling:
Twitch channel:
AI provider and model:
Voice and microphone:
OBS version and capture sources:
Network scenarios completed:
Memory at 0 / 30 / 60 / final minutes:
Checks passed:
Checks failed:
Logs, screenshots, or recording links:
Issues created:
Final result: pass / fail
```
