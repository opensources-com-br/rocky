# Desktop application

Application entry point for macOS and Windows, targeting the JVM.

This module will assemble shared UI, data adapters, and desktop services through constructor injection. It owns window lifecycle and application startup/shutdown.

Planned dependencies: `shared/core`, `shared/data`, `shared/ui`, and `platform/desktop`.

## Run locally

From the repository root:

```shell
./gradlew :apps:desktop:run
```

The current window uses native decorations for dragging, resizing, and system minimize controls. Rocky also provides actions to minimize, stay on top, and switch between expanded and compact sizes.
