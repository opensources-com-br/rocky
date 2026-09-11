# Avaliação de prontidão do MVP

Data: 10/09/2026. Código analisado: `b930ad518eccaa990260167f8457bdd43dfe9c37`.

Esta avaliação é histórica. As correções posteriores e os critérios de teste do candidato 1.0.10-alpha.8 estão no [roteiro de validação de hoje](TEST_TODAY.pt-BR.md). Os achados abaixo descrevem o commit analisado, não o estado atual de cada funcionalidade.

## Parecer

O Rocky tem uma base de alpha funcional, mas ainda não tem evidência suficiente para lançamento amplo. Recomendo fechar primeiro um MVP focado em Twitch: conectar o próprio canal, pedir uma síntese por texto, receber resposta com fontes, ouvir opcionalmente e salvar/exportar notas. É uma proposta de redução de escopo, não uma alteração já aprovada do plano.

O MVP original de `PLAN.pt-BR.md` inclui YouTube, Super Chat, resumo pós-live e outras capacidades ainda ausentes. Se esse escopo for mantido, essas entregas também bloqueiam sua conclusão.

## O que foi verificado

- `JAVA_HOME=…/temurin-17.0.19/Contents/Home ./gradlew build`: passou localmente no macOS; 49 testes, zero falhas, erros ou testes ignorados nos relatórios XML.
- [CI do commit analisado](https://github.com/opensources-com-br/rocky/actions/runs/34489856097): build e empacotamento aprovados em macOS e Windows.
- [Release v1.0.8-alpha.2](https://github.com/opensources-com-br/rocky/releases/tag/v1.0.8-alpha.2): DMG, MSI, EXE e SHA256SUMS publicados. Essa release antecede os commits mais recentes; o candidato a lançamento deve ser identificado por commit e checksum.
- Leitura dos módulos de UI, domínio, Twitch, IA, notas, voz, preferências, workflows e documentação.
- A consulta das issues abertas e fechadas não retornou registros; não encontrei evidências de execução do protocolo com streamers nas fontes consultadas.

Limites: não executei uma live real, requisições autenticadas de IA, instalação limpa ou gravação no OBS. Não fiz auditoria visual manual. Testes automatizados e inspeção de código não certificam essas experiências.

## Capacidades presentes

| Área | Situação observada |
| --- | --- |
| Base desktop | KMP/Compose, módulos separados, janela nativa, fixação e alternância de tamanho |
| Twitch | Device Flow, EventSub, deduplicação limitada, keepalive, reconexão e refresh em 401 na assinatura |
| IA | Ollama/OpenAI, contexto limitado a 30 mensagens, validação de IDs citados, teste de conexão e timeout |
| Voz | TTS do sistema, interrupção, seleção de microfone e transcrição por whisper.cpp externo |
| Notas | SQLite, salvar sugestões, editar, excluir, recuperar ao reabrir e exportar Markdown |
| Privacidade básica | Tokens/chaves sem persistência; OpenAI recebe `store=false`; áudio temporário removido no encerramento normal da transcrição |
| Distribuição | CI nos dois sistemas, instaladores alpha, checksums, landing bilíngue e protocolo manual |

Não há necessidade evidente de reescrever a arquitetura ou adicionar conta/backend próprios para esse recorte.

## Prioridades antes de lançamento amplo

### 1. Recuperação confiável da Twitch

**Problema confirmado por leitura do código:** `DesktopTwitchChatClient.checkTokenValidation()` encerra a conexão em qualquer erro da validação periódica, incluindo timeout ou falha temporária de rede. `fail()` desativa a sessão. A renovação de token existe na assinatura do chat, mas não nesse caminho de validação.

Impacto: uma interrupção que coincida com a validação pode exigir reconexão manual, mesmo com o mecanismo de recuperação implementado.

Entrega: distinguir falhas transitórias de autorização revogada; aplicar retry limitado/backoff e renovação quando cabível; manter o estado visível e cancelar recuperação ao desconectar. Testar o conector real com transporte e relógio controláveis, incluindo validação periódica, expiração e troca de sessão.

Fonte: `shared/data/src/desktopMain/kotlin/dev/rocky/data/twitch/DesktopTwitchChatClient.kt`, especialmente `checkTokenValidation`, `fail` e `subscribeToChat`.

### 2. Completar a interação mínima com o assistente

- **Não existe campo de pedido textual ao assistente.** Há análise genérica por botão; o pedido personalizado chega pelo microfone, dependente do whisper.cpp. Acrescentar entrada textual e envio/cancelamento para tornar o fluxo útil sem STT.
- **O modo compacto não exibe a sugestão em sessão real.** `CompactContent` recebe o texto, mas escolhe sempre “Chat real da Twitch” quando `real=true`. Mostrar a resposta e disponibilizar interrupção de áudio no modo usado sobre o jogo/OBS.
- **Onboarding exige teste de voz.** `FirstUseContent` só conclui com Twitch, IA e voz aprovadas. Permitir concluir em modo texto e ativar voz depois.
- **Conectar Twitch exige registrar um aplicativo e colar Client ID.** Para público não técnico, distribuir configuração pública do aplicativo Rocky e deixar Client ID próprio em opções avançadas. O client ID público não é um segredo; titularidade e configuração externa ainda precisam ser resolvidas.

Aceite: uma pessoa em máquina limpa conecta Twitch, configura IA e faz uma pergunta digitada sem compilar nada nem instalar transcrição.

Fontes: `RockyWindow.kt`, `FirstUseContent.kt`, `PlatformSettings.kt` e `TwitchDesktopPreferences.kt`.

### 3. Evidências e informações corretas

- `ChatMessage` não guarda horário de recebimento/origem ou identidade estável do autor. O contexto usa as últimas 30 mensagens; isso não implementa “últimos dois minutos” nem contagem confiável de pessoas.
- A UI apresenta quantidade de fontes, mas não oferece inspeção das mensagens citadas. Conversa mostra apenas as últimas cinco mensagens.
- Ao salvar uma sugestão, `RockyWindow` cria `LiveNote` com timestamp literal “agora” e sem IDs/textos das evidências. Após reabrir, perde-se o contexto que justificou a nota.
- `TwitchLiveState.platforms` fixa `messagesPerMinute = 0`, mesmo recebendo chat. A contagem de mensagens na UI é o tamanho do buffer, limitado a 1.000, não o total recebido na sessão.

Entrega: timestamps, identificação de sessão/canal, evidências preservadas nas notas, abertura das fontes e métrica calculada ou explicitamente indisponível. Identificar períodos de desconexão e não prometer cobertura completa.

Aceite: pedir síntese de um intervalo conhecido, conferir as fontes e exportar uma nota com horário e contexto verificáveis. Nunca mostrar zero como substituto de métrica não implementada.

Fontes: `shared/core/.../live/LiveModels.kt`, `RockyWindow.kt`, `ConversationContent.kt`, `shared/data/.../db/Note.sq`.

### 4. Validar IA e voz nas condições de uso

- O teste da OpenAI verifica disponibilidade do modelo por GET; não valida uma geração real com o schema e parâmetros usados. Ollama considera variantes com o mesmo nome antes de `:` equivalentes no teste. Validar o modelo exato e uma resposta mínima.
- A análise automática começa habilitada. Torná-la uma escolha explícita; o plano original prevê uso sob demanda como padrão.
- Existe intervalo de análise, mas não expiração de sugestões, medição de consumo ou orçamento de sessão. Sugestão não descartada bloqueia novas análises automáticas.
- Cancelar o job de IA descarta resultados antigos, mas o transporte usa `HttpClient.send` bloqueante dentro de `Dispatchers.Default`; a interrupção efetiva da chamada não está demonstrada. Implementar e testar cancelamento do transporte, sem prometer reversão de cobrança já incorrida.
- A interface permite volume, mas o comando de voz do macOS não utiliza `volumePercent`. Não há seleção de dispositivo de saída; a saída padrão precisa ser considerada no teste OBS.
- O comando Windows é testado como lista de argumentos, sem execução real de TTS nesse teste. Validar no Windows texto PT-BR, aspas, pontuação e caracteres especiais passando por PowerShell.
- A UI tem opção de inglês, mas o prompt força sugestões em português e o teste de voz também usa frase em português. Alinhar idioma ou limitar a promessa do primeiro lançamento.

Aceite: geração real nos dois provedores escolhidos, comportamento compreensível em timeout/429/modelo ausente, cancelamento testado, saída de voz previsível e gravação OBS aprovada.

Fontes: `AiSuggestionState.kt`, `AiSuggestionPrompt.kt`, `OpenAiSuggestionClient.kt`, `OllamaAiClient.kt`, `VoiceState.kt`, `DesktopVoiceService.kt` e `DesktopVoiceServiceTest.kt`.

### 5. Testar o candidato com streamers

Executar o protocolo já escrito em [STREAMER_TEST.pt-BR.md](STREAMER_TEST.pt-BR.md):

- instalação limpa em macOS e Windows 11, sem Java separado;
- três sessões reais com dois streamers, incluindo uma de duas horas;
- OBS em ambos os sistemas;
- interrupções de 15 s e 60 s, troca de rede e cancelamento de reconexão;
- preservação das notas, consumo de memória e resposta da interface ao longo da sessão.

O soak test atual injeta 20.000 eventos diretamente em `TwitchLiveState` usando cliente falso. É útil para verificar o limite do buffer, mas não mede duas horas reais, áudio, IA, OAuth, sockets ou desempenho com OBS.

Aceite: registros com versão, commit, checksum, hardware, resultados e problemas encontrados; nenhum crash, perda de notas, mistura de canais ou áudio inesperado na transmissão.

### 6. Preparar a entrega pública

- Os instaladores atuais são declaradamente sem assinatura. Para lançamento amplo, planejar assinatura/notarização no macOS e assinatura no Windows, com validação dos pacotes resultantes. Alpha assistida pode manter a limitação explícita.
- Fixar e publicar arquitetura/SO realmente testados. `macos-latest` sozinho não comprova suporte a Intel.
- Atualizar a landing: o exemplo principal mistura Twitch/YouTube/Kick e “sete pessoas nos últimos dois minutos”, capacidades não implementadas no fluxo real. Rotular como conceito ou usar exemplo fiel ao produto entregue.
- Acrescentar política operacional de dados, como apagar dados/configurações, canal de suporte e reporte de vulnerabilidades. Há exclusão individual de notas; não há painel de dados/retenção ou limpeza geral.
- Publicar limitações conhecidas e instruções de atualização. A automação de release publica sempre prerelease; definir conscientemente o canal ao promover uma versão.

Aceite: o usuário consegue baixar o pacote correto, instalar, entender as dependências/limitações e obter ajuda sem depender do desenvolvedor.

## Diferenças em relação ao MVP original

| Entrega prevista | Estado atual | Recorte Twitch recomendado |
| --- | --- | --- |
| YouTube e Super Chats | Não implementados; aba de apoios demonstrativa | Adiar e comunicar claramente |
| Fila persistida de apoios e prevenção de releitura | Ausente | Adiar junto com apoios |
| Ideias de sessão real | Lista vazia; exportação disponível só para fixtures | Adiar ou ocultar a aba no modo real |
| Resumo pós-live | Sem pipeline de resumo ou histórico de sessões | Adiar; entregar notas/exportação utilizáveis |
| Notas manuais livres | Não há ação “Nova nota”; criação parte de sugestão | Adicionar se o MVP prometer anotações livres |
| Cofre do sistema | Ausente; credenciais somente em memória | Pode ficar para depois, com necessidade de reautorizar explícita |
| Atalho global, tray e persistência da janela | Não encontrados; tamanho/fixação mantidos só na execução | Priorizar após fluxo central, conforme teste com streamers |
| Kick/Facebook, mobile, lojas e escuta contínua | Fora do escopo inicial | Manter fora |

## Ordem de execução recomendada

1. Definir explicitamente Twitch como escopo inicial ou manter o plano completo.
2. Corrigir recuperação periódica da Twitch e completar pedido textual/modo compacto.
3. Corrigir fontes, timestamps, notas e métricas; simplificar onboarding.
4. Validar geração real, cancelamento, TTS e roteamento OBS.
5. Executar testes com streamers sobre um único candidato, corrigir bloqueios e repetir os cenários afetados.
6. Finalizar pacotes, documentação, comunicação e lançar gradualmente.

Não atribuo um percentual de conclusão: compilar, passar testes e ter funcionalidades visíveis não têm o mesmo peso que completar uma live real sem perda de dados. O ponto de decisão é cumprir os critérios acima.
