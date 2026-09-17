# Desktop application

Application entry point for macOS and Windows, targeting the JVM.

This module assembles shared UI, data adapters, and desktop services through constructor injection. It owns window lifecycle and application startup/shutdown.

Dependencies: `shared/core`, `shared/data`, `shared/ui`, and `platform/desktop`.

## Run locally

From the repository root:

```shell
./gradlew :apps:desktop:run
```

On macOS, Rocky starts as a menu bar app without a Dock icon. Its menu provides `Rocky` to open the main window, `Settings` to open configuration in a separate window, and `Quit` to finish the session and exit. The main and settings windows can remain open independently. Closing either window keeps Rocky running in the menu bar.

On Windows, Rocky starts in the notification area, normally under the taskbar overflow arrow. Its native tray menu provides `Rocky`, `Settings`, and `Quit`; the first two open independent windows, while closing either window keeps the app running in the tray. The window uses native decorations and retains always-on-top, compact mode, persisted bounds, and the global show/hide shortcut.
