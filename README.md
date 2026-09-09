# Rocky

Live assistant for macOS and Windows: the voice of chat, with notes and ideas during a stream.

Rocky is being bootstrapped with Kotlin Multiplatform and Compose Multiplatform planned for the application, local data, and AI chosen by the user. The monorepo directories are in place; build configuration and executable code come next.

Read the [implementation plan](docs/PLAN.md) for the product, privacy, integrations, architecture, monorepo, phases, and release criteria.

Leia em [português](README.pt-BR.md).

## Repository structure

| Directory | Responsibility |
| --- | --- |
| [apps/desktop](apps/desktop/) | Desktop entry point and application lifecycle |
| [apps/web](apps/web/) | Landing page and public documentation |
| [shared/core](shared/core/) | Domain models, contracts, and intervention policy |
| [shared/data](shared/data/) | Chat connectors, AI providers, and persistence |
| [shared/ui](shared/ui/) | Shared Compose components and presentation state |
| [platform/desktop](platform/desktop/) | Native system integrations |
| [fixtures](fixtures/) | Synthetic demo and test inputs |
| [docs/adr](docs/adr/) | Architecture decision records |

Each directory documents its scope. Gradle modules, source sets, and dependency wiring will be added during build setup.
