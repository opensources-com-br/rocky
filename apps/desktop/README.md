# Desktop application

Application entry point for macOS and Windows, targeting the JVM.

This module assembles shared UI, data adapters, and desktop services through constructor injection. It owns window lifecycle and application startup/shutdown.

Dependencies: `shared/core`, `shared/data`, `shared/ui`, and `platform/desktop`.

## Run locally

From the repository root:

```shell
./gradlew :apps:desktop:run
```

The current window uses native decorations for dragging, resizing, and system minimize controls. Use the native title bar for minimizing and closing. Rocky provides always-on-top and compact controls, plus a global shortcut to show/hide the window. Expanded bounds are persisted; initial expanded size is 462 × 900 dp, settings 462 × 820 dp and compact 340 × 180 dp, subject to the available display area.
