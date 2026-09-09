# Shared core

Platform-independent domain models, service contracts, context handling, and intervention policy.

Other Kotlin modules depend on this module; it must not depend on them. Keep shared code free of UI, database, provider SDK, and desktop API dependencies.

Planned source sets: `commonMain` and `commonTest`.

Status: directory scaffold only; Gradle configuration and source code come next.
