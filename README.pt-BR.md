# Rocky

Assistente de live para macOS e Windows: a voz do chat, com notas e ideias durante a transmissão.

O Rocky está na etapa de estruturação, com Kotlin Multiplatform e Compose Multiplatform previstos para o aplicativo, dados locais e IA escolhida pela pessoa usuária. Os diretórios do monorepo estão criados; a configuração de build e o código executável são os próximos passos.

Leia o [plano de implementação](docs/PLAN.pt-BR.md), com produto, privacidade, integrações, arquitetura, monorepo, etapas e critérios de lançamento. A versão principal também está disponível em [inglês](docs/PLAN.md).

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

Cada diretório documenta seu escopo. Os módulos Gradle, conjuntos de fontes e vínculos entre dependências serão adicionados durante a configuração de build.
