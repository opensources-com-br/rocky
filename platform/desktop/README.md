# Desktop platform services

Native/JVM implementations for macOS and Windows: Keychain/DPAPI credentials, preferences, local paths and data management, microphone capture, whisper.cpp transcription, system speech, PCM playback, global shortcuts and window bounds.

Depends on `shared/core`. The desktop application supplies these services to the UI and data adapters. No user plugin runtime or application tray/menu-bar flow is implemented.

Run `./gradlew :platform:desktop:desktopTest` from the repository root. Installed-app checks are still required for OS permissions, audio devices and credential storage on both systems.
