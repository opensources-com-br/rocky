# Rocky

Live assistant for macOS and Windows: the voice of chat, with notes and ideas during a stream.

Rocky is built with Kotlin Multiplatform and Compose Multiplatform. The desktop prototype includes a simulated live session, local SQLite notes, Markdown export, and a real Twitch chat connector.

Read the [implementation plan](docs/PLAN.md) for the product, privacy, integrations, architecture, monorepo, phases, and release criteria.

To test real chat, follow the [Twitch connection guide](docs/TWITCH.md).
To generate suggestions, follow the [AI provider guide](docs/AI.md).

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

Each directory documents its scope.
