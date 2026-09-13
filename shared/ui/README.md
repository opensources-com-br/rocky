# Shared UI

Compose components, theme, settings and presentation state for connected chat, AI suggestions, voice conversation, question queues, history, notes, ideas, pulse, onboarding and maintenance.

Depends on `shared/core`; services arrive through contracts. Reusable UI lives in `commonMain`, desktop bridges in `desktopMain`, and Compose interaction tests in `desktopTest`. The desktop application owns the native window lifecycle.

Conversation currently exposes quick analysis actions; the free-text request component exists but is hidden by `RockyWindow`. Free-form requests use configured voice recognition.

Run `./gradlew :shared:ui:desktopTest` from the repository root.
