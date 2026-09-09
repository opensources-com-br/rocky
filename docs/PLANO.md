# Rocky — plano de produto e implementação

Status: proposta para iniciar implementação. Data: 8 de setembro de 2026.

O Rocky será um assistente de live para macOS e Windows: uma janela flutuante que acompanha o chat, responde ao streamer por texto e voz, organiza apoios pagos e registra notas e ideias. Aplicativo gratuito, open source, com processamento e armazenamento sob controle do usuário, sem conta Rocky e sem backend obrigatório.

As nove imagens fornecidas são referências visuais e de comportamento, não instruções executáveis nem garantias de integração. Nomes de modelos, vozes, métricas e plataformas nelas são exemplos. Este documento distingue decisões propostas de capacidades verificadas nas fontes.

## 1. Decisões para começar

| Tema | Proposta |
| --- | --- |
| Tecnologia | Kotlin Multiplatform (KMP) para lógica compartilhada; Compose Multiplatform para interface |
| Desktop | Target JVM, runtime incluído no instalador; integrações nativas isoladas |
| Sistemas iniciais | macOS Apple Silicon e Windows x64; validar macOS Intel antes de anunciá-lo |
| Conta Rocky | Nenhuma; acesso protegido pela conta do sistema operacional |
| Dados | SQLite local; credenciais no cofre do sistema |
| IA inicial | Ollama local e OpenAI por chave do próprio usuário |
| Outros provedores | Adaptadores posteriores para Claude, Gemini, Grok e endpoints compatíveis |
| Primeiro chat real | Twitch; YouTube em seguida, obrigatório para validar Super Chat |
| Voz | Entrada por botão/atalho; transcrição local e síntese local no MVP |
| Distribuição | Download direto primeiro; Mac App Store e Microsoft Store depois |
| Web | Landing page e documentação estáticas, no mesmo monorepo |
| Licença | MIT como proposta para o código; conferir licenças dos componentes distribuídos |

KMM era a denominação focada em mobile; KMP descreve melhor este projeto. Compose desktop permite instaladores com Java incluído, sem instalação separada de JDK pelo usuário. Os pacotes devem ser construídos e verificados nos sistemas correspondentes. [Documentação Kotlin](https://kotlinlang.org/docs/multiplatform/compose-native-distribution.html).

Para desktop, as lojas são Mac App Store e Microsoft Store. Google Play fica para a fase Android. A distribuição direta permite validar o produto antes dos requisitos adicionais das lojas. A Apple exige sandbox para Mac App Store e oferece Developer ID/notarização para distribuição externa; a Microsoft aceita diferentes formatos, incluindo MSI/EXE sujeitos a requisitos. [Apple](https://developer.apple.com/macos/distribution/), [Microsoft](https://learn.microsoft.com/en-us/windows/apps/publish/publish-your-app/msi/app-package-requirements).

## 2. Como a pessoa usa

1. Instala e abre Rocky, sem cadastro.
2. Experimenta uma live simulada, sem credenciais, download de modelo ou microfone.
3. Escolhe processamento local ou IA em nuvem. A interface informa destino dos dados e eventuais custos externos.
4. No modo local, conecta um Ollama instalado e seleciona um modelo já disponível; se não houver, recebe instruções. Downloads são explícitos, mostrando tamanho e licença.
5. No modo nuvem, informa sua chave de API, escolhe modelo e testa a conexão. A chave nunca aparece no histórico.
6. Autoriza Twitch/YouTube no navegador oficial e escolhe canal/live.
7. Escolhe microfone e saída de áudio, testa a voz e configura um atalho.
8. Inicia a sessão. Pode digitar ou segurar o botão para falar com Rocky.
9. Encerra e revisa resumo, pendências e ideias, com exportação Markdown/JSON.

Exemplo principal: o streamer pergunta ao público “qual tema vocês querem?”. Ele pede “Rocky, resume as sugestões dos últimos dois minutos”. Rocky agrupa respostas, explica as opções mais citadas e permite inspecionar as mensagens de origem. O resultado diz “entre as mensagens recebidas”, sem fingir que representa todos os espectadores.

Uma segunda versão permitirá ao streamer ativar escuta contínua: nesse modo Rocky poderá acompanhar perguntas e promessas espontâneas. Com entrada apenas por botão, ele não conhece tudo que foi falado na live. Notas sobre promessas só podem vir de transcrição autorizada, comando explícito ou registro manual; sugestões derivadas apenas do chat devem ser identificadas como tal.

## 3. Local, offline e segurança

Não precisamos de login/senha próprios: isso criaria recuperação de contas e infraestrutura sem benefício para a primeira versão. São diferentes: abrir Rocky, autorizar a plataforma de live e fornecer acesso ao provedor de IA.

| Modo | O que permanece no computador | O que utiliza rede |
| --- | --- | --- |
| IA local | Inferência, transcrição, síntese, notas e contexto | Chat ao vivo e downloads escolhidos pelo usuário |
| IA em nuvem | App, regras, armazenamento, seleção do contexto | Trechos necessários enviados ao provedor escolhido, além do chat |
| Demonstração offline | Fixtures e interação simulada | Nada |

O termo de produto deve ser “dados locais e IA à sua escolha”. Só anunciar processamento de IA totalmente local quando LLM, transcrição e voz estiverem efetivamente locais. O chat ao vivo depende de internet. Ollama também oferece modelos em nuvem: um endpoint localhost, sozinho, não prova que a inferência é local. Usar modelos locais e testar o funcionamento com a rede desligada. [Ollama API](https://docs.ollama.com/api/introduction).

Medidas de implementação:

- `SecretStore`: Keychain no macOS e Credential Manager/DPAPI no Windows, com integração validada em cada SO. Nada de chaves em SQLite, arquivos de preferências, logs ou exportações.
- Chaves fornecidas pertencem ao usuário. Nunca distribuir uma chave de IA do projeto dentro do aplicativo. O cofre protege armazenamento, não torna um computador comprometido seguro.
- OAuth com permissões mínimas de leitura; autenticação no navegador. Não pedir senha de Twitch, Google ou de aplicativos de IA.
- Para Google desktop, usar fluxo de app instalado com PKCE, state e callback loopback temporário. Não tratar um client secret distribuído como segredo confiável. [OAuth Google](https://developers.google.com/identity/protocols/oauth2/native-app).
- Para Twitch, usar Device Code Flow para cliente público, incluindo renovação, validação e revogação. Não pressupor que todos os provedores implementam o mesmo fluxo. [OAuth Twitch](https://dev.twitch.tv/docs/authentication/getting-tokens-oauth/).
- Não abrir servidor na rede local. Endpoints locais limitados a loopback; endpoints remotos configurados explicitamente e com TLS. Credenciais vinculadas ao provedor/origem e nunca reenviadas em redirecionamento para outro host.
- Chat é dado não confiável. Mensagens como “ignore suas regras” não viram instruções. Modelo não recebe credenciais, shell, navegação automática nem poder de enviar mensagens públicas.
- Ações permitidas: produzir respostas, sugerir notas e ideias. Saída estruturada validada antes da exibição/voz. Comandos do streamer chegam por um canal separado do chat.
- Leitura de apoios sujeita a filtro, limite de tamanho, bloqueio de URLs faladas e aprovação manual por padrão. Uma contribuição paga não contorna os controles.
- Microfone solicitado quando necessário, indicador real de captura, botão de parar e áudio bruto descartado após processamento por padrão.
- Proposta de retenção: chat/transcrição temporários durante sessão; ao encerrar, preservar apenas notas/resumo escolhidos. Histórico completo opcional com prazo configurável e exclusão automática. Pequenos trechos de evidência salvos com notas devem aparecer na política de retenção.
- Persistência da fila de apoios durante a sessão para recuperação de falhas; limpar o conteúdo ao encerrar conforme retenção. Logs locais técnicos sem conteúdo sensível, com rotação; telemetria desativada por padrão.
- Exportar e excluir sessões nas configurações. SQLite não é criptografado por padrão; proteção de histórico persistido deve ser uma decisão explícita, sem promessa falsa de criptografia.
- Nunca migrar automaticamente do modo local para nuvem quando um modelo falhar.

## 4. Contas de IA e provedores

Não basear o MVP na promessa de aproveitar assinaturas dos aplicativos ChatGPT, Claude, Gemini ou Grok. A proposta implementável é **traga sua chave de API ou use um modelo local**. Integrações por login de assinatura só entram quando houver mecanismo oficial documentado e aplicável ao Rocky; não reutilizar cookies nem credenciais de outros clientes.

A API OpenAI documenta credenciais próprias para chamadas da aplicação. Isso não estabelece um login universal de ChatGPT para o Rocky. [OpenAI Docs — autenticação](https://developers.openai.com/api/reference/overview).

| Adaptador | Configuração | Fase |
| --- | --- | --- |
| Ollama | Endpoint local, modelo local disponível, teste de capacidade | MVP |
| OpenAI | Chave do usuário e modelo; Responses API | MVP |
| Claude | Chave e API oficial própria | Após MVP |
| Gemini | Credencial atualmente suportada pelo Google AI Studio e API oficial | Após MVP |
| Grok | Chave e API oficial do provedor | Após MVP |
| Compatível com OpenAI | Base URL, chave opcional e capacidades detectadas/testadas | Após MVP |

Claude e Gemini não serão tratados como se fossem a mesma API da OpenAI. Cada adaptador implementa streaming, cancelamento, limites, erros e tradução de saída estruturada conforme sua API. [Claude](https://platform.claude.com/docs/en/api/overview), [Gemini](https://ai.google.dev/gemini-api/docs/api-key), [Grok](https://docs.x.ai/overview).

Modelo de linguagem, transcrição e síntese de voz são escolhas separadas. Disponibilizar somente controles suportados pelo modelo; “criatividade” não será um slider genérico aplicado a qualquer API. O estilo nunca altera contagens factuais.

Economia: agrupar eventos, remover duplicações, limitar contexto e resposta, usar cache de análises e no máximo uma análise automática por vez. Não fazer uma chamada por mensagem. Mostrar tokens e custo estimado quando disponíveis, orçamento de sessão e opção de interromper chamadas; limites do app não garantem o total faturado pelo provedor.

## 5. Plataformas de live

| Plataforma | Caminho técnico | Compromisso inicial |
| --- | --- | --- |
| Twitch | EventSub WebSocket e OAuth público | Primeiro conector real; chat e reconexão |
| YouTube | Live Streaming API, `liveChatMessages.streamList`; fallback `list` respeitando intervalo/cota | Chat e Super Chats no MVP |
| Kick | Eventos documentados via webhooks | Spike posterior; não prometer conexão local simples |
| Facebook | APIs/permissões ainda não validadas neste plano | Fora do MVP; não mostrar como integração disponível |

EventSub tem conexão mantida pelo cliente desktop. Implementar keepalive, reconexão, recriação de assinaturas quando necessária e deduplicação dos eventos. Uma desconexão pode perder mensagens; nunca apresentar uma janela incompleta como histórico completo. [Twitch WebSocket](https://dev.twitch.tv/docs/eventsub/handling-websocket-events/).

YouTube fornece tipos de mensagem específicos de Super Chat e Super Sticker. Validar autenticação, cotas, descoberta da live e encerramento do stream antes de fechar o conector. [Mensagens](https://developers.google.com/youtube/v3/live/docs/liveChatMessages), [streamList](https://developers.google.com/youtube/v3/live/docs/liveChatMessages/streamList).

Os webhooks documentados da Kick exigem um receptor alcançável pelo serviço. Isso cria uma decisão arquitetural: aguardar alternativa oficial adequada, permitir relay auto-hospedado opcional, ou oferecer infraestrutura opt-in mais tarde. Nenhum relay é requisito do MVP. Não depender de scraping ou protocolos internos. [Webhooks Kick](https://github.com/KickEngineering/KickDevDocs/blob/main/events/webhook-security.md).

Usar “Apoios” como aba agregada e “Super Chat” para o tipo do YouTube. Bits, inscrições e outras contribuições preservam a unidade e o tipo original. Não converter Bits para reais nem inventar equivalência entre plataformas. Valores monetários guardados como inteiro na unidade da API e código de moeda, nunca float.

Registro dos aplicativos OAuth, eventual verificação de consentimento, cotas e contas para testes são dependências de lançamento; não são cadastro Rocky. Builds de contribuidores poderão configurar seu próprio client ID. Testes simulados não dependem de segredos.

## 6. Comportamento do assistente

Fluxo: conectores → eventos normalizados → deduplicação/moderação → contexto limitado → agrupamento de perguntas → seleção de evidências → LLM → validação → política de intervenção → texto/voz/notas.

- `ChatEvent`: plataforma, canal, ID externo, autor, horário de origem e recebimento, texto, tipo e dados de apoio/moderação.
- `LiveSession`: começo/fim, canais e intervalos de falha de captura.
- `TopicCluster`: tema, mensagens de origem, número de mensagens e autores distintos por plataforma.
- `Intervention`: evidências, prioridade, criação/expiração e estado.
- `SupportEvent`: ID estável, moeda/unidade, leitura pendente/em reprodução/lida/ignorada.
- `Note` e `Idea`: timestamp da sessão, origem manual/IA, evidências e estado de revisão.
- `ProviderProfile`: provedor, modelo, capacidades e referência à credencial, nunca a chave.

Regras determinísticas controlam momento e frequência; o LLM sintetiza o conteúdo. Contagens vêm dos eventos, não de números gerados pelo modelo. “Sete pessoas” requer sete autores distintos no escopo conhecido; entre plataformas, não é possível deduplicar pessoas sem identidade vinculada. Preferir “sete mensagens” ou “sete contas”.

Política inicial proposta: modo sob demanda como padrão; modo proativo opt-in, teto de três intervenções por dez minutos, cooldown e respostas curtas. Prioridade: comando direto do streamer, apoio selecionado para leitura, pergunta repetida relevante, ideia. Ideias não podem bloquear indefinidamente apoios.

Estados: aguardando, capturando comando, analisando, falando, pausado, degradado. “Ouvindo chat” e “microfone ativo” são indicadores diferentes. Botão de silêncio interrompe áudio; pausa de sessão interrompe coleta/análise conforme indicado pela UI.

Fila única de fala, cancelamento imediato, descarte de ideias vencidas. Apoios não são descartados silenciosamente por limite de fila. Persistir estados para evitar releitura por reconexão; após crash durante áudio, marcar estado incerto e pedir decisão de reprodução ao usuário, sem alegar entrega exatamente uma vez.

Para escuta contínua futura: detecção de fala local, esperar inicialmente três segundos de silêncio e pausar intervenções quando o streamer falar. Suprimir a própria voz no reconhecimento. O MVP por botão evita depender dessa etapa para funcionar.

Resumo pós-live usa notas e resumos incrementais com evidências, respeitando retenção. “Live inteira” significa contexto resumido e recuperado, não enviar todas as mensagens a cada chamada. Clips são sugestões de timestamp, sem gravação ou corte de vídeo no MVP.

## 7. Janela e referências visuais

Preservar fundo escuro, acento laranja, destaque tipográfico da fala, cartões e abas das imagens. Tipografia real precisa de licença de redistribuição e boa legibilidade. Não copiar dimensões em pixels do screenshot como especificação de layout.

- Modo expandido inicial próximo de 420 × 680 dp; modo compacto próximo de 340 × 180 dp. Valores propostos, a validar em telas reais.
- Arrastar pelo cabeçalho, redimensionar, minimizar, fixar sempre no topo e restaurar pelo tray/menu bar.
- Fixar posição é um controle diferente de manter acima de outras janelas. Persistir ambos e impedir que a janela fique inacessível após remover um monitor.
- Sem roubar foco quando chega uma mensagem; respeitar atalhos e padrões de cada sistema. Não replicar botões macOS no Windows.
- Testar múltiplos monitores, DPI, fullscreen e Spaces. “Sempre no topo” não é garantia sobre todo modo exclusivo de jogo.
- Abas: Conversa, Apoios, Notas, Ideias e Pulso. Configurações: Agente, IA, Voz, Plataformas e Dados/Privacidade.
- Estados vazios e erros: não conectado, sem live, modelo indisponível, cota esgotada, sem permissão de microfone e reconectando.
- Pulso mostra mensagens/minuto calculadas localmente; espectadores apenas se a API fornecer, com horário da medição. Soma entre plataformas não é público único. Dado ausente aparece indisponível, não zero.
- “Salvar como nota” mantém timestamp e evidência; “Próxima” descarta a intervenção atual de modo explícito; “Silenciar” mostra estado reversível.
- Botões e foco acessíveis, navegação por teclado e texto redimensionável; cor de plataforma acompanhada de nome.

O texto “só você ouve; não entra na transmissão” precisa ser substituído por “saída de monitoramento”, com teste guiado do OBS. Captura geral do áudio do desktop pode transmitir a voz do Rocky. O app deve permitir escolher saída quando suportado e documentar roteamento por sistema. [Guia OBS](https://obsproject.com/kb/application-audio-capture-guide).

## 8. Arquitetura e monorepo

Começar com poucos módulos Gradle; conectores e provedores como pacotes internos até haver motivo para separar builds.

```text
rocky/
  apps/
    desktop/               # ponto de entrada JVM, janela e composição de dependências
    web/                   # landing page e docs estáticas
  shared/
    core/                  # domínio, contratos, contexto, política de intervenção
    data/                  # conectores, provedores, SQLite e repositórios
    ui/                    # componentes Compose e estado de apresentação
  platform/
    desktop/               # cofre, áudio, atalhos, tray e APIs do sistema
  docs/
    PLANO.md
    adr/                   # decisões de arquitetura à medida que forem implementadas
  fixtures/                # eventos sintéticos e cenários de replay
  .github/workflows/
  gradle/libs.versions.toml
  settings.gradle.kts
  build.gradle.kts
  README.md
  CONTRIBUTING.md
  SECURITY.md
  LICENSE
```

Lógica compartilhada em commonMain sem AWT, java.io ou dependência de um sistema. Implementações desktop em jvmMain/módulo de plataforma. Contratos: `ChatConnector`, `LlmProvider`, `SpeechRecognizer`, `SpeechSynthesizer`, `AudioOutput`, `SecretStore`, `SessionRepository`, `WindowController`.

Stack proposta: Coroutines/Flow, kotlinx.serialization, Ktor Client e SQLite via SQLDelight, sujeita à verificação de compatibilidade das versões no bootstrap. Injeção por construtores inicialmente. Estado imutável apresentado via StateFlow. Fixar toolchain/versões e lockfiles; evitar versões dinâmicas.

Transcrição candidata: whisper.cpp em processo auxiliar local com protocolo restrito; testar PT-BR, cancelamento, consumo e empacotamento. Síntese inicial: vozes locais do sistema atrás de adaptador; validar renderização e escolha da saída. Vozes variam por computador; Aurora/Íris/Nuno dos mockups não são recursos garantidos. [whisper.cpp](https://github.com/ggml-org/whisper.cpp).

Runtime de IA inicialmente externo (Ollama), sem empacotar modelos grandes nem instalação silenciosa. Um gerenciador de modelos integrado será etapa posterior se a experiência justificar.

Web: proposta Astro + conteúdo Markdown/MDX para apresentação, instalação, provedores, privacidade, áudio/OBS, contribuição e changelog. Compartilhar marca e tokens visuais, sem exigir a mesma tecnologia de UI do app. Sem backend, login ou chaves de usuário na landing page. [Conteúdo Astro](https://docs.astro.build/en/guides/content-collections/).

Mobile fica para depois. Compartilhar domínio, conectores e parte da UI não implica copiar a janela desktop. Planejar primeiro um companion para consultar notas/controlar a sessão; funcionamento autônomo e comportamento em segundo plano precisam de validação específica em iOS/Android. Não incluir pareamento de rede na primeira versão.

## 9. Entregas e critérios de conclusão

Estimativas orientativas para uma pessoa experiente dedicada, sem incluir espera por revisão de lojas/provedores. Ajustar depois dos spikes; não são prazos garantidos.

| Etapa | Estimativa | Entrega e condição de conclusão |
| --- | --- | --- |
| 0 — Viabilidade | 2–4 dias | Janela nos dois SOs; provas de cofre, áudio, OAuth e recebimento de chat; riscos registrados |
| 1 — Base utilizável | 3–5 dias | Monorepo, janela compacta/expandida, abas, fixtures, salvar/exportar nota, build em macOS e Windows |
| 2 — Primeiro fluxo real | 4–7 dias | Twitch → contexto → Ollama/OpenAI → resposta textual com evidências; reconexão e limites |
| 3 — Voz e YouTube | 5–8 dias | Push-to-talk, STT/TTS local, seleção/teste de áudio, YouTube e fila de Super Chat |
| 4 — Alpha pública | 4–7 dias | Resumo, instaladores testados, guia OBS, landing/docs, retenção e verificações de segurança |

Total inicial aproximado: 18–31 dias úteis, com incerteza concentrada em integrações nativas, áudio e aprovações externas. Primeiro resultado visual funcional na etapa 1; não esperar toda a alpha para usar o app.

Backlog inicial, em ordem:

1. Configurar Gradle Wrapper, JDK, catálogo de versões e CI macOS/Windows; build mínimo em ambos.
2. Criar domínio e relógio injetável, contratos e conector simulado com fixtures em PT-BR.
3. Implementar janela, alternância compacta, persistência de posição, tray e fixação.
4. Criar componentes dos mockups e estados reais de conexão/captura.
5. Implementar SecretStore e configurações sem vazamento de credenciais.
6. Registrar/configurar cliente público Twitch, Device Code Flow e EventSub.
7. Implementar pipeline limitado, contagens verificáveis, fontes e reconexão.
8. Implementar Ollama e OpenAI, teste de conexão, timeout e cancelamento.
9. Adicionar notas SQLite/exportação e retenção.
10. Integrar áudio local, atalho e corte imediato da fala.
11. Integrar YouTube e estados persistidos da fila de apoios.
12. Preparar instaladores, documentação pública e primeira alpha.

Fora do MVP: Kick/Facebook, envio automático ao chat, moderação com poder de banir, captura de tela, gravação de vídeo, clipping automático, escuta contínua, clonagem de voz, sincronização, plugins executáveis e aplicativos mobile.

## 10. Validação e lançamento

Testes essenciais: deduplicação/reconexão; contagem por autor; pressão de chat com fila limitada; eventos de apoio repetidos; expiração de sugestões; interrupção de voz; ausência de evidência; prompt injection no chat; timeout/429; schema inválido; exclusão de histórico e remoção de credenciais.

Usar replay sintético para qualidade: perguntas repetidas, sarcasmo, desacordo, spam, rajada de apoios e chat vazio. Avaliar relevância, fidelidade das contagens e número de interrupções indesejadas. Nenhum teste exige live ou chave real por padrão.

Metas propostas para benchmark, não resultados medidos: parada de voz abaixo de 300 ms no p95; resposta textual p95 abaixo de 5 s após receber o comando no perfil de referência; sessão de 2 h a 100 mensagens/s com memória limitada e sem travamento de UI. Medir CPU/GPU e impacto no OBS; publicar hardware/modelo utilizado e separar memória do app da memória da IA. Revisar metas após etapa 0.

Gate da alpha: pessoa consegue instalar em máquina limpa, conectar uma live, perguntar por voz/texto, receber síntese apoiada em mensagens, ler Super Chat sem duplicar após reconexão, salvar notas e encerrar/exportar. Modo local processa fixture sem rede. Validar exclusão do áudio da transmissão com gravação de teste no OBS.

CI compila e testa nos dois sistemas; release gera artefatos por arquitetura, checksums e assinatura/notarização conforme canal. Proteger segredos de assinatura, publicar instruções de build e inventário de dependências/licenças. Na alpha, notificar atualização e abrir download oficial; não instalar atualização durante live. Atualização automática exige projeto próprio de verificação e rollback.

MIT é a recomendação inicial de licença do código, ainda não aplicada neste plano. Documentar contribuição, reporte de vulnerabilidades e política de dados. Modelos/vozes e recursos gráficos precisam de licenças próprias compatíveis antes de serem redistribuídos. Rocky terá identidade gráfica e voz próprias; as referências de ficção orientam o conceito, sem pressupor disponibilidade de nome, domínio ou materiais oficiais.

Pendências externas a resolver durante a implementação: titular/contas dos apps OAuth e publicação, credenciais de teste, certificados de assinatura, disponibilidade do nome nas lojas e hardware para validar Windows. Nenhuma impede o bootstrap local com fixtures.
