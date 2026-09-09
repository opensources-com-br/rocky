# Shared UI

Compose components, theme, screens, and presentation state.

Planned dependency: `shared/core`. Receive services through its contracts; do not reference concrete data adapters or desktop APIs.

Keep reusable UI in `commonMain`. The desktop application owns window lifecycle and provides platform capabilities.

Status: directory scaffold only; Gradle configuration and source code come next.
