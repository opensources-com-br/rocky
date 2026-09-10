# Rocky

Rocky é um assistente local para transmissões ao vivo no macOS e Windows. Ele acompanha o chat, encontra perguntas recorrentes e sinais relevantes, sugere o que o streamer pode dizer e guarda notas e ideias da sessão.

O projeto é desenvolvido como um monorepo com Kotlin Multiplatform, Compose Multiplatform, SQLDelight e Gradle.

> Rocky está atualmente em uma versão inicial do protótipo desktop. Os instaladores são builds de desenvolvimento sem assinatura e a integração real oferece suporte apenas à Twitch.

[Read in English](README.md).

[Site](https://opensources-com-br.github.io/rocky/pt-BR/) · [Versões alpha](https://github.com/opensources-com-br/rocky/releases)

## O que funciona hoje

- janela desktop móvel, redimensionável, fixável sobre outros apps e com modo compacto;
- sessões de demonstração e de live real claramente separadas;
- autenticação da Twitch pelo Device Code Flow, chat ao vivo pelo EventSub e reconexão automática;
- sugestões fundamentadas no chat usando modelos locais do Ollama ou a Responses API da OpenAI;
- leitura local por meio das vozes do macOS e Windows;
- comandos push-to-talk com transcrição local pelo `whisper.cpp`;
- notas locais em SQLite, com criação, edição, exclusão, recuperação e exportação em Markdown;
- exportação em Markdown das ideias exibidas na demonstração;
- builds automatizados e instaladores de desenvolvimento para macOS e Windows;
- interface em inglês e português brasileiro, selecionada pelo idioma do sistema e ajustável nas Configurações.

Rocky não exige uma conta própria nem um backend remoto. Tokens de acesso da Twitch e API keys da OpenAI permanecem em memória. As notas ficam armazenadas localmente no computador. Quando um provedor de IA em nuvem é selecionado, as mensagens usadas como contexto são enviadas a esse provedor.

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

1. Siga o [guia de conexão com a Twitch](docs/TWITCH.pt-BR.md) para registrar um cliente público e conectar o canal do próprio streamer.
2. Siga o [guia de provedores de IA](docs/AI.pt-BR.md) para usar um modelo local do Ollama ou uma API key da OpenAI.
3. Opcionalmente, siga o [guia de voz](docs/VOICE.pt-BR.md) para escolher uma voz do sistema e configurar a transcrição local por push-to-talk.
4. Inicie uma live na Twitch, conecte o Rocky e envie uma mensagem por outra conta. As novas mensagens aparecerão na aba Conversa.

O conector da Twitch atualmente lê apenas novas mensagens do chat. Contagem de espectadores, inscrições, pontos do canal, Super Chats, mensagens anteriores e outras plataformas ainda não estão integrados.

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
| [apps/web](apps/web/) | Landing page estática bilíngue publicada pelo GitHub Pages |
| [shared/core](shared/core/) | Modelos de domínio, contratos e regras de exportação |
| [shared/data](shared/data/) | Implementações da Twitch, IA e SQLite |
| [shared/ui](shared/ui/) | Interface Compose e estado de apresentação |
| [platform/desktop](platform/desktop/) | Arquivos, navegador, preferências, áudio e transcrição nativos |
| [fixtures](fixtures/) | Dados sintéticos de demonstração e testes |
| [docs/adr](docs/adr/) | Registros de decisões de arquitetura |

O [plano de implementação](docs/PLAN.pt-BR.md) descreve a direção do produto, o modelo de privacidade, a arquitetura, as etapas de entrega e os critérios de lançamento.

## Licença

Rocky é um software de código aberto disponibilizado sob a [Licença MIT](LICENSE).
