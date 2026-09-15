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

On Windows, the app keeps its existing window lifecycle for now. The window uses native decorations for dragging, resizing, and system minimize controls. Rocky provides always-on-top and compact controls, plus a global shortcut to show/hide the window. Expanded bounds are persisted; initial expanded size is 462 × 900 dp, settings 462 × 820 dp and compact 340 × 180 dp, subject to the available display area.
