# Desktop validation

This document tracks build and native-window validation for Rocky. The English version is canonical; see [VALIDATION.pt-BR.md](VALIDATION.pt-BR.md) for Portuguese.

## Automated validation

Every push to `main` and every pull request runs the complete Gradle build on macOS and Windows. The workflow also creates native installers and uploads them as GitHub Actions artifacts for seven days:

| Runner | Tasks | Artifact |
| --- | --- | --- |
| `macos-latest` | `build`, `packageDmg` | `rocky-macos` containing a `.dmg` |
| `windows-latest` | `build`, `packageMsi`, `packageExe` | `rocky-windows` containing `.msi` and `.exe` installers |

The packages are currently unsigned development builds. Signing and macOS notarization belong to the release stage.

The automated suite also runs a Twitch presentation-state soak test with 20,000 messages and seven simulated connection drops. It verifies that recovery preserves the active session and that memory remains bounded to the latest 1,000 chat messages.

## Local commands

Use JDK 17, then run:

```shell
./gradlew build
./gradlew :apps:desktop:run
./gradlew :apps:desktop:packageDmg
```

On Windows PowerShell:

```powershell
.\gradlew.bat build
.\gradlew.bat :apps:desktop:run
.\gradlew.bat :apps:desktop:packageMsi :apps:desktop:packageExe
```

Generated packages are written below `apps/desktop/build/compose/binaries/main/`.

## Native window smoke test

Run this checklist on macOS and on a real Windows installation or emulator. Test the packaged application because it includes the same runtime and metadata as the installer artifact.

- [ ] Install and launch Rocky without a system Java installation.
- [ ] Move the window using the native title bar.
- [ ] Resize the expanded window and confirm it cannot become smaller than 340 × 180.
- [ ] Minimize from Rocky's yellow control and restore it from the Dock or taskbar.
- [ ] Pin the window, place another application over it, and confirm Rocky remains visible; unpin it and confirm normal stacking returns.
- [ ] Switch to compact mode and confirm the window becomes 340 × 180.
- [ ] Return to expanded mode and confirm the previous size is restored.
- [ ] Open settings and confirm the window becomes 420 × 820; close settings and confirm the previous size returns.
- [ ] Close Rocky using its red control.

## Streamer test

Use the [streamer alpha test protocol](STREAMER_TEST.md) for clean installation, a two-hour live session, OBS audio/window capture, network interruption, and evidence requirements. Submit each run with the **Streamer alpha test** issue form.

The automated soak test does not replace this manual gate. A release candidate remains unvalidated until the required macOS, Windows, OBS, and real-stream records pass.

## Validation record

Record each manual run in an issue or pull request using this template:

```text
Date:
Rocky commit:
Operating system and version:
Architecture:
Display resolution and scaling:
Package tested (.dmg, .msi, or .exe):
Checklist result: pass / fail
Problems found:
```

Current automated checkpoint (2026-09-10): the complete build, UI tests, prolonged-session soak test, and native packaging pass on GitHub runners for macOS and Windows. The streamer protocol remains a manual release gate and requires recorded results from real streams.

For the current candidate, build identity, signing and remaining manual checks, see [release preparation](RELEASE_PREPARATION.md).
