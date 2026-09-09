# Shared UI

Compose components, theme, screens, and presentation state.

Planned dependency: `shared/core`. Receive services through its contracts; do not reference concrete data adapters or desktop APIs.

Keep reusable UI in `commonMain`. The desktop application owns window lifecycle and provides platform capabilities.

Current scope: Rocky's dark theme, header, and window controls. Live-session screens and state come next.
