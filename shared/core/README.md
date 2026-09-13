# Shared core

Platform-independent models, service contracts and rules for chat context, agent profiles, audio, questions, records, backups and updates.

Implemented in `commonMain`, with portable tests in `commonTest` and a JVM target named `desktop`. Other Kotlin modules depend on this module; it has no UI, database-driver or desktop API dependencies.

Run `./gradlew :shared:core:desktopTest` from the repository root.
