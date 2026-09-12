# Rocky

Rocky é um assistente local para transmissões ao vivo no macOS e Windows. Ele acompanha o chat, encontra perguntas recorrentes e sinais relevantes, sugere o que o streamer pode dizer e guarda notas e ideias da sessão.

O projeto é desenvolvido como um monorepo com Kotlin Multiplatform, Compose Multiplatform, SQLDelight e Gradle.

> Rocky está atualmente em uma versão inicial do protótipo desktop. Os instaladores são builds de desenvolvimento sem assinatura. A integração de chat real está disponível para Twitch, Kick e YouTube; a Kick exige encaminhamento por webhook HTTPS público.

[Read in English](README.md).

[Site](https://opensources-com-br.github.io/rocky/pt-BR/) · [Versões alpha](https://github.com/opensources-com-br/rocky/releases)

## O que funciona hoje

- janela desktop móvel, redimensionável, fixável sobre outros apps e com modo compacto;
- autenticação da Twitch pelo Device Code Flow, chat ao vivo pelo EventSub e reconexão automática;
- OAuth da Kick, webhooks de chat assinados e assinatura de eventos;
- OAuth para desktop, descoberta de live ativa e consulta do chat do YouTube;
- contagem de espectadores e mensagens por minuto da live atual na Twitch, Kick e YouTube;
- sugestões fundamentadas no chat usando modelos locais do Ollama, a API da OpenAI ou OpenRouter;
- leitura local por meio das vozes do macOS e Windows;
- comandos por voz com palavra de ativação e transcrição local pelo `whisper.cpp`;
- notas locais em SQLite, com criação, edição, exclusão, recuperação e exportação em Markdown;
- builds automatizados e instaladores de desenvolvimento para macOS e Windows;
- interface em inglês e português brasileiro, selecionada pelo idioma do sistema e ajustável nas Configurações.

Rocky não exige uma conta própria nem inclui um backend remoto. Configurações das plataformas e da IA ficam no computador. Os Client Secrets da Kick e do YouTube e as chaves de IA salvas usam o cofre do sistema; tokens das plataformas permanecem na memória. As notas ficam em um banco SQLite local. Quando um provedor de IA em nuvem é selecionado, as mensagens usadas como contexto são enviadas a esse provedor.

## Executar pelo código-fonte

Requisitos:

- macOS ou Windows;
- JDK 17;
- Git.

Clone o repositório e execute o aplicativo desktop:

```shell
git clone https://github.com/opensources-com-br/rocky.git
cd rocky
./gradlew :apps:desktop:run
```

No Windows PowerShell:

```powershell
git clone https://github.com/opensources-com-br/rocky.git
cd rocky
.\gradlew.bat :apps:desktop:run
```

Execute todas as verificações automatizadas com `./gradlew build` ou `.\gradlew.bat build`.

## Configurar uma sessão real

1. Siga o guia de conexão com a [Twitch](docs/TWITCH.pt-BR.md), [Kick](docs/KICK.pt-BR.md) ou [YouTube](docs/YOUTUBE.pt-BR.md). A Kick exige uma URL HTTPS pública que encaminhe os webhooks assinados ao Rocky.
2. Siga o [guia de provedores de IA](docs/AI.pt-BR.md) para usar Ollama local, OpenAI API ou OpenRouter.
3. Siga o [guia de voz](docs/VOICE.pt-BR.md) para preparar a transcrição local e testar a conversa por áudio.
4. Inicie uma live, conecte o Rocky e envie uma mensagem por outra conta. As novas mensagens aparecerão na aba Conversa.

Os conectores da Twitch, Kick e YouTube leem novas mensagens e a contagem atual de espectadores. Eventos de apoio, pontos do canal, Super Chats, mensagens anteriores e Facebook ainda não estão integrados.

## Gerar instaladores

```shell
./gradlew :apps:desktop:packageDmg
```

```powershell
.\gradlew.bat :apps:desktop:packageMsi :apps:desktop:packageExe
```

Os pacotes são gerados em `apps/desktop/build/compose/binaries/main/`. Consulte o [guia de validação desktop](docs/VALIDATION.pt-BR.md) para ver a matriz de builds e a lista de testes manuais.

Versões alpha com tag publicam o DMG do macOS, o MSI e EXE do Windows e checksums SHA-256 nos [Releases do GitHub](https://github.com/opensources-com-br/rocky/releases).

Antes de promover uma alpha, siga o [protocolo de teste com streamers](docs/STREAMER_TEST.pt-BR.md) para instalação limpa, live prolongada, OBS e recuperação da conexão.

## Estrutura do repositório

| Diretório | Responsabilidade |
| --- | --- |
| [apps/desktop](apps/desktop/) | Ponto de entrada, empacotamento e ciclo de vida do aplicativo desktop |
| [apps/web](apps/web/) | Landing, documentação e showcase do Rocky em Next.js |
| [shared/core](shared/core/) | Modelos de domínio, contratos e regras de exportação |
| [shared/data](shared/data/) | Implementações da Twitch, Kick, YouTube, IA e SQLite |
| [shared/ui](shared/ui/) | Interface Compose e estado de apresentação |
| [platform/desktop](platform/desktop/) | Arquivos, navegador, preferências, áudio e transcrição nativos |
| [docs/adr](docs/adr/) | Registros de decisões de arquitetura |

O [plano de implementação](docs/PLAN.pt-BR.md) descreve a direção do produto, o modelo de privacidade, a arquitetura, as etapas de entrega e os critérios de lançamento.

## Licença

Rocky é um software de código aberto disponibilizado sob a [Licença MIT](LICENSE).

O código atual cobre Twitch, Kick, YouTube, IA, voz opcional, perguntas agrupadas, notas, ideias, momentos e resumo dos registros locais ao desconectar ou fechar o app. Kick e YouTube ainda precisam de validação com credenciais reais antes do lançamento; a Kick também precisa de webhook público. Super Chats e geração automática de ideias ficam fora deste candidato. Consulte [preparação de lançamento](docs/RELEASE_PREPARATION.md) e [dados e privacidade](docs/PRIVACY.md).
