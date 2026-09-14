# Rocky

Rocky é um assistente local para transmissões ao vivo no macOS e Windows. Ele acompanha o chat, encontra perguntas recorrentes e sinais relevantes, sugere o que o streamer pode dizer e guarda notas e ideias da sessão.

O projeto é desenvolvido como um monorepo com Kotlin Multiplatform, Compose Multiplatform, SQLDelight e Gradle.

> Rocky está atualmente em uma versão inicial do protótipo desktop. A integração de chat real está disponível para Twitch, Kick, YouTube, Facebook e TikTok LIVE; a Kick exige encaminhamento por webhook HTTPS público e o TikTok usa uma conexão WebCast não oficial.

[Read in English](README.md).

[Site](https://opensources-com-br.github.io/rocky/) · [Versões alpha](https://github.com/opensources-com-br/rocky/releases)

## O que funciona hoje

- janela desktop móvel, redimensionável, fixável sobre outros apps e com modo compacto;
- autenticação da Twitch pelo Device Code Flow, chat ao vivo pelo EventSub e reconexão automática;
- OAuth da Kick, webhooks de chat assinados e assinatura de eventos;
- OAuth para desktop, descoberta de live ativa e consulta do chat do YouTube;
- OAuth de Páginas, descoberta de live ativa e consulta do chat do Facebook;
- chat do TikTok LIVE por conexão desktop com o nome de usuário público;
- conexões simultâneas com Twitch, Kick, YouTube, Facebook e TikTok, com audiência por plataforma, total agregado e mensagens por minuto;
- sugestões fundamentadas no chat usando modelos locais do Ollama, a API da OpenAI ou OpenRouter;
- leitura pelas vozes do macOS/Windows ou ElevenLabs, com chave própria, streaming e alternativa local opcional;
- comandos por voz com palavra de ativação e transcrição local pelo `whisper.cpp`;
- notas locais em SQLite, com criação, edição, exclusão, recuperação e exportação em Markdown;
- builds automatizados e instaladores de desenvolvimento para macOS e Windows;
- interface em inglês e português brasileiro, selecionada pelo idioma do sistema e ajustável nas Configurações.

Rocky não exige uma conta própria nem inclui um backend remoto. Configurações das plataformas e da IA ficam no computador. Os Client Secrets da Kick e do YouTube e as chaves de IA/ElevenLabs salvas usam o cofre do sistema; tokens das plataformas permanecem na memória. As notas ficam em um banco SQLite local. Quando um provedor de IA em nuvem é selecionado, as mensagens usadas como contexto são enviadas a esse provedor. ElevenLabs recebe o texto a ser falado quando selecionado.

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

Execute as verificações Kotlin com `./gradlew build` ou `.\gradlew.bat build`.

A web é validada separadamente: em `apps/web`, execute `npm ci`, `npm test`, `npm run lint` e `npm run build` (Node.js 22, como no CI).

## Configurar uma sessão real

1. Siga os guias das plataformas que deseja conectar: [Twitch](docs/TWITCH.pt-BR.md), [Kick](docs/KICK.pt-BR.md), [YouTube](docs/YOUTUBE.pt-BR.md), [Facebook](docs/FACEBOOK.pt-BR.md) e [TikTok](docs/TIKTOK.pt-BR.md). É possível manter mais de uma conexão ativa; a Kick exige uma URL HTTPS pública que encaminhe os webhooks assinados ao Rocky.
2. Siga o [guia de provedores de IA](docs/AI.pt-BR.md) para usar Ollama local, OpenAI API ou OpenRouter.
3. Siga o [guia de voz](docs/VOICE.pt-BR.md) para preparar a transcrição local e testar a conversa por áudio.
4. Inicie uma live, conecte o Rocky e envie uma mensagem por outra conta. As novas mensagens aparecerão na aba Conversa.

Os conectores da Twitch, Kick, YouTube, Facebook e TikTok leem novas mensagens e a contagem atual de espectadores. Quando há várias conexões, o app identifica quantas estão online, mostra a audiência compactada em cada plataforma e soma esses valores no total do rodapé. Eventos de apoio, pontos do canal, Stars, Super Chats, presentes e mensagens anteriores ainda não estão integrados. O Facebook exige um app Business e uma live ativa em uma Página autorizada. O TikTok depende do protocolo WebCast não oficial e do serviço de conexão Eulerstream.

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
| [shared/data](shared/data/) | Implementações da Twitch, Kick, YouTube, Facebook, TikTok, IA e SQLite |
| [shared/ui](shared/ui/) | Interface Compose e estado de apresentação |
| [platform/desktop](platform/desktop/) | Arquivos, navegador, preferências, áudio e transcrição nativos |
| [docs/adr](docs/adr/) | Registros de decisões de arquitetura |

O [plano de implementação](docs/PLAN.pt-BR.md) descreve a direção do produto, o modelo de privacidade, a arquitetura, as etapas de entrega e os critérios de lançamento.

## Licença

Rocky é um software de código aberto disponibilizado sob a [Licença MIT](LICENSE).

O código atual cobre Twitch, Kick, YouTube, Facebook, TikTok, IA, voz opcional, perguntas agrupadas, notas, ideias, momentos e resumo dos registros locais ao desconectar ou fechar o app. Kick, YouTube, Facebook e TikTok ainda precisam de validação em lives reais antes do lançamento; a Kick também precisa de webhook público. Super Chats, Stars, presentes e geração automática de ideias ficam fora deste candidato. Consulte [preparação de lançamento](docs/RELEASE_PREPARATION.md) e [dados e privacidade](docs/PRIVACY.md).
