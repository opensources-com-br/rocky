# Rocky

Assistente de live para macOS e Windows: a voz do chat, com notas e ideias durante a transmissão.

O Rocky é desenvolvido com Kotlin Multiplatform e Compose Multiplatform. O protótipo desktop inclui uma live simulada, notas locais em SQLite, exportação em Markdown e um conector real para o chat da Twitch.

Leia o [plano de implementação](docs/PLAN.pt-BR.md), com produto, privacidade, integrações, arquitetura, monorepo, etapas e critérios de lançamento. A versão principal também está disponível em [inglês](docs/PLAN.md).

Para testar o chat real, siga o [guia de conexão com a Twitch](docs/TWITCH.pt-BR.md).
Para gerar sugestões, siga o [guia de provedores de IA](docs/AI.pt-BR.md).

Read in [English](README.md).

## Estrutura do repositório

| Diretório | Responsabilidade |
| --- | --- |
| [apps/desktop](apps/desktop/) | Ponto de entrada desktop e ciclo de vida do aplicativo |
| [apps/web](apps/web/) | Landing page e documentação pública |
| [shared/core](shared/core/) | Modelos de domínio, contratos e política de intervenção |
| [shared/data](shared/data/) | Conectores de chat, provedores de IA e persistência |
| [shared/ui](shared/ui/) | Componentes Compose compartilhados e estado de apresentação |
| [platform/desktop](platform/desktop/) | Integrações nativas com o sistema |
| [fixtures](fixtures/) | Dados sintéticos para demonstração e testes |
| [docs/adr](docs/adr/) | Registros de decisões de arquitetura |

Cada diretório documenta seu escopo.
