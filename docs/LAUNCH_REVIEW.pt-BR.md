# Avaliação de lançamento — Rocky 1.0.10-alpha.17

Data: 12/09/2026. Commit: `7ba03c4c0298881f50ed5c903b923a6c7b6d0089`.

**Parecer: base funcional para testes acompanhados; lançamento público amplo ainda não recomendado.** O principal trabalho restante é fechar o fluxo real, proteger credenciais e comprovar estabilidade com Twitch, voz e OBS. Não encontrei motivo para reescrever a arquitetura ou acrescentar backend/conta Rocky para entregar esse recorte.

Esta avaliação descreve o código atual e complementa a avaliação histórica de 10/09. Recomendo um primeiro lançamento focado em Twitch, IA, voz e notas. Isso é uma recomendação de escopo: o plano original também exige YouTube, Super Chats e resumo pós-live, ainda ausentes.

## Evidência verificada

| Verificação | Resultado |
| --- | --- |
| Estado inicial do checkout | Sem alterações locais |
| Build local, JDK 17.0.19, macOS ARM64 | `./gradlew build :apps:desktop:packageDmg`: aprovado em 46 s |
| Suíte completa | 107 testes; zero falhas, erros ou testes ignorados nos XML da execução completa |
| Capturas adicionais | Dois testes de captura executados novamente; aprovados. Inspecionada a imagem da sessão Twitch simulada |
| CI do mesmo commit | Build e empacotamento aprovados em macOS e Windows |
| Distribuição | Alpha.17 publicada com DMG, MSI, EXE e SHA256SUMS |
| Testes com streamers | A consulta de issues abertas/fechadas não retornou registros; os documentos continuam tratando esses testes como pendentes |

[Build no GitHub](https://github.com/opensources-com-br/rocky/actions/runs/34661559135), [workflow da release](https://github.com/opensources-com-br/rocky/actions/runs/34661566191), [artefatos alpha.17](https://github.com/opensources-com-br/rocky/releases/tag/v1.0.10-alpha.17).

O número de 107 refere-se à execução completa anterior à execução seletiva de capturas, que substitui os relatórios locais de UI. A captura usa serviços falsos; não representa uma live autenticada. Não executei instalação limpa, sessão real de duas horas, geração autenticada nos provedores, microfone/TTS reais ou OBS. Windows foi verificado pelo CI, não manualmente. Não fiz auditoria completa de acessibilidade nem de vulnerabilidades de dependências. Ausência de registros nas fontes consultadas não prova que ninguém tenha testado fora delas.

## Situação por área

| Área | O que existe | O que falta para liberar |
| --- | --- | --- |
| Arquitetura | Separação entre domínio, dados, UI e plataforma; contratos e testes | Mais testes dos fluxos montados em `RockyWindow` e dos transportes reais |
| Twitch | Device Flow, EventSub, deduplicação, keepalive, reconexão, validação/renovação, audiência | OAuth em instalação limpa, recuperação real, cobertura de falhas/expiração e indicação de dados desatualizados |
| IA | Ollama, OpenAI e OpenRouter; teste com geração; IDs de fontes validados; análise automática opt-in | Credenciais protegidas e isoladas, erros acionáveis, cobertura explícita do contexto e validação real por modelo |
| Voz | TTS nativo, captura local, whisper.cpp, palavra de ativação, indicador do microfone | Recuperação ao interromper fala, teste de continuidade, instalação simples e medição com OBS |
| Notas | SQLite, migração do formato antigo, edição/exclusão, fontes, data com fuso e Markdown | Falhas de exportação/inicialização tratadas; dados de sessão/canal; opção de limpeza e recuperação |
| Interface | Janela compacta/expandida, fixação, fontes, chat rolável, PT-BR/EN | Ligar pergunta textual/cancelamento, resolver onboarding e alinhar idioma do assistente |
| Site e documentação | Landing bilíngue, download, indicação de alpha e limitações | Eliminar contradições de voz, credenciais e Client ID; informar arquiteturas e atualização |
| Distribuição | Build nos dois sistemas, instaladores e checksums | Instalação/upgrade reais, assinatura conforme canal, identificação inequívoca do build |

## Correções prioritárias

### 1. P1 — Chaves persistidas fora de um cofre e documentação contraditória

`AiDesktopPreferences.configuration` lê e grava `apiKey` em `java.util.prefs.Preferences`. Não existe integração com Keychain/DPAPI/Credential Manager. A tela de IA informa que salva a chave, mas o onboarding diz que credenciais sensíveis não são salvas; `docs/AI.pt-BR.md` ainda afirma que a chave é descartada ao fechar.

**Impacto:** uma credencial com acesso à conta de IA fica em armazenamento comum, e o usuário recebe informações incompatíveis sobre sua retenção. Não foi constatado vazamento para terceiros; o problema observado é de implementação e comunicação.

**Entrega:** armazenar no cofre do sistema, migrar/remover o valor legado das preferências e disponibilizar remoção da credencial. Se o cofre for adiado, retornar explicitamente ao armazenamento somente em memória. Unificar os textos em ambos os idiomas.

**Aceite:** reiniciar mantém a credencial apenas pelo mecanismo escolhido; preferências, exportações e logs não contêm a chave; apagar credencial realmente a remove.

Fontes: `platform/desktop/.../AiDesktopPreferences.kt:23,30`; `shared/ui/.../FirstUseContent.kt:68`; `shared/ui/.../AiSettings.kt`; `docs/AI.pt-BR.md:21,30`.

### 2. P1 — A chave acompanha a troca de provedor/origem

`AiSuggestionState.updateProvider()` altera provedor, endpoint e modelo por `copy`, preservando `apiKey`. `updateEndpoint()` também preserva a chave. O adaptador envia esse valor no cabeçalho Authorization para o endereço configurado. A validação atual só verifica campos vazios, sem exigir HTTPS para APIs remotas.

**Reprodução pelo fluxo de código:** configurar chave OpenAI, selecionar OpenRouter e clicar em testar sem substituir a chave. A requisição ao OpenRouter usa a credencial anterior. Também é possível configurar um endpoint HTTP remoto e enviar a chave sem TLS.

**Entrega:** vincular credencial ao provedor/origem e limpar ou carregar o perfil correspondente quando isso mudar. Exigir HTTPS em APIs remotas; preservar suporte explícito a Ollama local.

**Aceite:** testes com chaves sintéticas e servidores controlados demonstram que uma chave nunca acompanha automaticamente uma mudança de origem e que endpoints inseguros são rejeitados antes do envio.

Fontes: `shared/ui/.../AiSuggestionState.kt:60-79`; `shared/data/.../DesktopAiSuggestionClient.kt` (`validationError`); `shared/data/.../OpenAiSuggestionClient.kt` (`request`).

### 3. P1 — Pergunta por texto e cancelamento não chegam à tela real

O componente `StreamerTextRequest` existe, mas `RockyWindow` chama `ConversationContent` apenas com mensagens e transcrição. Os parâmetros `textRequestEnabled`, `showTextRequest` e `analyzing` permanecem falsos, e os callbacks permanecem vazios. O campo e o botão de cancelar ficam ocultos.

O teste `sendsTypedStreamerRequest` monta somente o componente isolado. Por isso passa mesmo com o fluxo principal indisponível. A captura da tela montada também mostra a ausência do campo.

**Entrega:** conectar habilitação, envio, geração em andamento e cancelamento ao estado de IA na janela real. Preservar entrada/estado adequados em erros e reconexão.

**Aceite:** um teste que monta `RockyWindow` conecta a Twitch simulada, digita um pedido, verifica que ele chega ao cliente de IA, cancela e envia outro. O mesmo fluxo deve funcionar no instalador sem whisper.cpp.

Fontes: `shared/ui/.../RockyWindow.kt:406`; `ConversationContent.kt:32-40,70`; `shared/ui/src/desktopTest/.../RockyVisualCaptureTest.kt:405`.

### 4. P1 — Interromper fala pode deixar o ouvinte sem capturar novamente

No comando por voz, a captura é retomada pelo callback `onFinished = voice::resumeListener`. `stopSpeaking()` cancela o job e invalida sua geração, sem executar esse callback ou retomar o ouvinte. Os botões Silenciar e Próxima chamam esse método. `listenerEnabled` pode continuar verdadeiro enquanto captura e transcrição permanecem paradas.

**Cenário derivado do código, ainda sem reprodução em áudio real:** fazer uma pergunta por voz, interromper a resposta e chamar Rocky novamente. O caminho de retomada foi cancelado; o usuário pode precisar desligar e religar o ouvinte. Retomar o áudio não implementa, por si só, a retomada da captura.

**Entrega:** definir transições explícitas para concluir, interromper e falhar na fala, preservando separadamente a intenção de ouvir. A fila também precisa preservar o callback das falas enfileiradas, hoje reduzidas a texto.

**Aceite:** teste integrado de pergunta → resposta → Silenciar/Próxima → nova pergunta; nenhum estado “ativo” sem trabalho de captura; cancelamento de sessão não reativa o microfone.

Fontes: `shared/ui/.../VoiceState.kt:246-288,416-432`; `RockyWindow.kt` (`analyzeVoiceCommand`, `onNext`, `onSilence`).

### 5. P1 para adoção pública — Onboarding dependente de voz externa

`firstUseReady()` exige Twitch, IA **e voz**. A voz é considerada preparada quando os caminhos não estão vazios e o TTS foi testado; isso não demonstra que microfone, modelo e transcrição funcionam. No Windows, a instalação do whisper.cpp/modelo é manual; no Mac, a preparação automática exige Homebrew existente.

Há uma contradição objetiva com `STREAMER_TEST.pt-BR.md`, que exige concluir sem voz e ainda cita push-to-talk. O candidato atual descreve escuta por palavra de ativação.

**Entrega recomendada:** permitir concluir em texto e configurar voz depois. Se voz obrigatória for uma decisão de produto, fornecer preparação compatível com o público-alvo e exigir sucesso do teste de conversa, não apenas caminhos preenchidos. Atualizar o protocolo para o comportamento escolhido.

**Aceite:** uma pessoa em máquina limpa completa o fluxo anunciado sem ajuda do desenvolvedor; caminhos inválidos não são apresentados como transcrição pronta.

Fontes: `FirstUseContent.kt` (`firstUseReady`); `RockyWindow.kt:329`; `VoiceState.kt` (`transcriptionReady`); `DesktopVoiceService.kt` (`automaticTranscriptionSetupSupported`, `prepareTranscription`).

## Ajustes de confiabilidade e produto

| Prioridade | Achado | Ação e critério de conclusão |
| --- | --- | --- |
| P2 | Exportar Markdown executa `writeText` sem tratamento de erro até o callback da UI | Capturar falha de escrita, apresentar erro e permitir tentar outro destino. Testar pasta sem permissão/volume indisponível sem encerrar o app |
| P2 | O repositório SQLite é criado diretamente em `main`, antes do tratamento de carregamento das notas | Exibir recuperação para banco inacessível/corrompido e preservar o arquivo original; testar upgrade com notas reais de versões anteriores |
| P2 | Voz filtra dois minutos de recebimento, mas o prompt corta para 200 mensagens; análise por botão usa últimas 30, sem filtro temporal | Informar amostragem/limites e lacunas de rede. Não apresentar isso como cobertura integral de dois minutos ou da live |
| P2 | `ChatMessage` não tem ID estável de autor, horário de origem ou canal; notas guardam texto das fontes, mas não identificação da sessão | Acrescentar metadados necessários à rastreabilidade; não prometer contagem exata de pessoas sem cálculo verificável |
| P2 | Falha ao atualizar audiência é ignorada, mantendo o último número | Mostrar horário/frescor ou indisponibilidade; simular erro após uma leitura bem-sucedida |
| P2 | Erro de geração vira mensagem genérica; OpenRouter pode tentar três respostas inválidas | Distinguir timeout, cota, credencial e schema; mostrar ação recomendada. Medir latência e consumo; não prometer cancelamento de cobrança |
| P2 | Captura usa blocos de oito segundos e pausa para transcrição e resposta | Medir comandos cortados na fronteira e perda de fala durante processamento; ajustar captura ou comunicar claramente quando pode falar |
| P2 | Interface EN não altera prompt, frases faladas ou idioma padrão de transcrição (`pt`) | Alinhar idioma ponta a ponta ou declarar PT-BR como idioma do assistente no primeiro lançamento |
| P2 | Nome do agente é configurável, mas a palavra de ativação permanece fixa em Rocky e variantes | Reconhecer o nome configurado ou manter a instrução de ativação fixa; hoje a tela promete reação ao novo nome |
| P2 | Download do modelo usa `openStream`, URL mutável e validação apenas por tamanho | Acrescentar timeout/cancelamento e verificação de integridade; erro deve permitir nova tentativa sem prender configuração |
| P2 | Volume no Mac não é aplicado pelo comando `say`; saída usa o dispositivo padrão | O texto já explica a limitação. Desabilitar o controle sem efeito no Mac e validar o roteamento OBS por sistema |
| P2 | Não há fluxo central de limpar dados/configurações e credenciais, nem guia operacional completo | Documentar localização, retenção, exclusão e recuperação; permitir apagar dados escolhidos sem apagar outros silenciosamente |

Fontes principais: `DesktopNoteExporter.kt`, `Main.kt`, `AiSuggestionPrompt.kt`, `LiveModels.kt`, `SuggestionNote.kt`, `DesktopTwitchChatClient.kt` (`checkAudience`), `AiSuggestionState.kt`, `DesktopVoiceService.kt`, `VoiceCommand.kt`, `VoiceSettings.kt`.

## O que já melhorou e não deve ser tratado como ausente

- Validação periódica da Twitch agora tenta renovar tokens e aplica retry a falhas que não exigem nova autorização.
- O modo compacto mostra a sugestão e tem controle de silêncio.
- Sugestões podem abrir fontes; notas persistem IDs, trechos das evidências e data com fuso.
- Mensagens/minuto e total da sessão são calculados; audiência vem da API.
- Testar conexão de IA realiza uma geração curta, além da consulta inicial ao provedor.
- O cancelamento de IA usa trabalho interrompível; o problema prioritário é sua ausência na interação real.
- Análise automática começa desligada e é configurável.
- Landing e abas não apresentam mais os antigos dados fictícios como uma sessão real.

Isso não transforma testes simulados em validação das integrações reais, mas reduz o volume de implementação restante em relação à avaliação histórica.

## Distribuição e documentação

**Client ID Twitch:** o guia da alpha.17 afirma que o pacote não inclui ID oficial. Porém, a variável `ROCKY_TWITCH_CLIENT_ID` existe no repositório, o workflow injeta a variável e o CI consultado registra empacotamento com valor não vazio. O código lê a propriedade como fallback. Não inspecionei o conteúdo dos instaladores remotos nem validei a titularidade/tipo desse aplicativo OAuth. Portanto, a pendência é conferir o artefato publicado e o fluxo real, e alinhar a documentação; não se pode afirmar simplesmente que falta cadastrar a variável.

**Pacotes:** os instaladores são declaradamente sem assinatura e não há configuração de assinatura/notarização no build lido. Para beta acompanhada, a limitação pode permanecer explícita. Para público amplo, recomendo fechar assinatura/notarização e testar os pacotes resultantes em máquinas limpas.

**Versão e arquitetura:** os arquivos usam `1.0.10`, enquanto a versão lógica é `1.0.10-alpha.17`. Isso torna diferentes alphas difíceis de identificar e exige teste de atualização, especialmente no Windows. Expor versão completa/commit no app e nas notas, manter versão nativa compatível com upgrades e informar arquitetura/SO efetivamente testados. A matriz atual não constitui evidência de macOS Intel.

**Operação:** publicar limitações conhecidas, instruções de atualização sem perda de notas, política de dados coerente e orientação de suporte/reporte privado de vulnerabilidades. O link público de issues já existe; falta organizar o processo e a informação. O workflow de release sempre usa `--prerelease`; promoção a estável precisa ser deliberada.

## Escopo que ainda não foi entregue

| Capacidade | Estado e recomendação |
| --- | --- |
| YouTube e Super Chats | Sem conector/fila. Bloqueiam o MVP original; podem ser adiados se o primeiro lançamento for explicitamente Twitch |
| Ideias de sessão | Lista real vazia e exportação desabilitada. Ocultar ou manter claramente “em breve” |
| Resumo pós-live e histórico de sessões | Sem pipeline. Adiar no recorte Twitch ou implementar antes de anunciar |
| Notas livres | Não há ação de criar nota manual; criação começa ao salvar uma sugestão. Não anunciar editor livre sem essa ação |
| Tray, atalho global e persistência de janela | Não encontrados no fluxo de entrada. Priorizar conforme testes com streamers |
| Kick, Facebook, mobile, sincronização | Manter fora do primeiro lançamento |

## Sequência recomendada e decisão de liberação

1. **Fechar o candidato técnico:** corrigir os itens P1, ligar texto/cancelamento e definir onboarding. Acrescentar testes dos fluxos montados, credenciais com dados sintéticos e retomada de voz.
2. **Fechar experiência e recuperação:** tratar exportação/banco, erros de IA, idioma, limites de contexto e configuração real da Twitch. Executar somente os testes adicionais pertinentes às mudanças.
3. **Gerar um candidato identificável:** versão, commit, checksums, arquitetura e instruções correspondentes ao pacote. Congelar esse candidato para testes.
4. **Executar o protocolo real:** instalação limpa em macOS e Windows 11; três sessões com dois streamers, uma de duas horas; OBS nos dois sistemas; quedas de 15/60 s, troca de rede e cancelamento da recuperação. Registrar memória, latência, qualidade das fontes e retomada após interrupção de voz.
5. **Liberar gradualmente:** beta acompanhada depois das correções e do fluxo mínimo validado; público amplo somente com os critérios de instalação, estabilidade, dados e distribuição cumpridos.

O protocolo existente deve ser atualizado antes de executá-lo, pois contém passos incompatíveis com o candidato atual. A ausência de crash em testes unitários não satisfaz o critério de uma live completa. Qualquer perda de notas, áudio inesperado no OBS, ouvinte sem retomada ou conexão irrecuperável reprova o candidato até correção e repetição do cenário afetado.

Não atribuo percentual de conclusão ou prazo fechado: falta evidência das integrações em condições reais, e ela determina o esforço de estabilização. O próximo marco útil é **um streamer instalar, conectar, perguntar, interromper, perguntar novamente, salvar/exportar e terminar uma live sem assistência técnica**.
