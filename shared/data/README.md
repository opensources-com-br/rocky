# Shared data

Chat connectors, AI provider adapters, persistence, and repository implementations behind the contracts in `shared/core`.

Planned dependency: `shared/core`. Receive platform services through its contracts; do not reference the desktop application or UI.

Keep portable logic in `commonMain` and platform-specific drivers in their target source sets. Start with connectors and providers as internal packages.

Status: directory scaffold only; Gradle configuration and source code come next.
