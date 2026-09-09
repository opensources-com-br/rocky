# Desktop validation

This document tracks build and native-window validation for Rocky. The English version is canonical; see [VALIDATION.pt-BR.md](VALIDATION.pt-BR.md) for Portuguese.

## Automated validation

Every push to `main` and every pull request runs the complete Gradle build on macOS and Windows. The workflow also creates native installers and uploads them as GitHub Actions artifacts for seven days:

| Runner | Tasks | Artifact |
| --- | --- | --- |
| `macos-latest` | `build`, `packageDmg` | `rocky-macos` containing a `.dmg` |
| `windows-latest` | `build`, `packageMsi`, `packageExe` | `rocky-windows` containing `.msi` and `.exe` installers |

The packages are currently unsigned development builds. Signing and macOS notarization belong to the release stage.

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
- [ ] Open settings and confirm the window becomes 420 × 520; close settings and confirm the previous size returns.
- [ ] Close Rocky using its red control.

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

Current macOS checkpoint (2026-09-09, Apple Silicon): the complete build, Compose UI tests, `.dmg` creation, and packaged application launch passed. Native window interaction remains a manual check. Windows CI packaging and the real Windows window test must pass before this milestone is complete.
