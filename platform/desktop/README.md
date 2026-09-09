# Desktop platform services

Implementations of system services for macOS and Windows: credential storage, audio, shortcuts, tray/menu bar, and native window operations.

Planned dependency: `shared/core`. Keep JVM and native operating-system APIs here, behind the shared contracts.

The desktop application selects and supplies these implementations at startup. Shared modules must not depend on this module.

Status: directory scaffold only; Gradle configuration and source code come next.
