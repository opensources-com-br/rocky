# Rocky — visão geral e melhorias para o MVP

Revisão em 12/09/2026, baseada no código local até o commit 72c5fda.
Registro original do overview. Em uma etapa posterior, os dez itens escolhidos pelo usuário foram implementados: histórico, continuidade, prioridade, atalhos, fim da fala, ditado livre, desfazer, configuração simplificada, pré-live e persistência da janela. As tabelas abaixo preservam o diagnóstico original; os demais itens continuam como propostas.
As prioridades são hipóteses de produto baseadas no código e nos problemas relatados pelo usuário, ainda sem pesquisa com streamers.

## Objetivo do produto

Ajudar o streamer a acompanhar o chat, obter respostas curtas e guardar assuntos úteis sem interromper a transmissão. O ciclo central é conectar → perguntar → receber uma resposta fundamentada → continuar falando → salvar algo útil.

A recomendação é consolidar esse ciclo na Twitch. Novas plataformas aumentam o escopo de autenticação, testes e suporte e podem esperar a validação da experiência principal.

## O que existe hoje

| Área | Implementado | Limite atual |
| --- | --- | --- |
| Desktop | Compose/Kotlin para macOS e Windows; janela redimensionável, sempre no topo, modo compacto e ícone próprio | Não há persistência de posição/tamanho nem controles globais completos |
| Layout | Principal e configurações com 462 × 820 dp; compacto com 340 × 180 dp | Validar monitores pequenos, escala e fullscreen |
| Twitch | Autorização no navegador, EventSub, renovação de tokens na sessão, reconexão e deduplicação | Só chat Twitch; autorização não é preservada entre execuções |
| Contexto | Até 200 mensagens recentes dos últimos dois minutos; buffer visual limitado a 1.000 mensagens; aviso de lacunas | Não é memória da live inteira; chat movimentado pode exceder a amostra |
| IA | Ollama, OpenAI e OpenRouter; teste de conexão, cancelamento, respostas com fontes, pedidos repetidos | Uma resposta corrente; sem histórico conversacional enviado ao modelo |
| Automação | Análise automática opcional com intervalo e lote mínimo de mensagens | Sem fila de perguntas respondidas/pendentes nem deduplicação semântica de sugestões |
| Voz | Whisper local, vozes do sistema, nome de ativação, testes de microfone, retorno à escuta | Captura em blocos de oito segundos; pausas durante transcrição e síntese; dependências externas |
| Notas e ideias | Salvar resposta pela UI ou por voz; categorias persistidas em SQLite, edição, exclusão e exportação | Não há criação livre por ditado nem organização por sessão; ideias usam uma categoria no repositório de notas |
| Métricas | Espectadores e mensagens/minuto, expiração de dados antigos, duração e tokens parciais de IA | Pulso ainda simples; a barra usa divisor fixo 820, sem uma referência útil ao streamer |
| Configuração | PT-BR/inglês, nome/tom do agente, frequência, provedor/modelo, voz e microfone | Ainda expõe campos técnicos no caminho inicial; verificação de IA não é persistida |
| Dados | Chaves de IA protegidas pelo sistema, limpeza explícita, exportação e recuperação de erro do banco | Integração nativa e atualização de instalação ainda precisam da validação real descrita nos roteiros |
| Distribuição | Workflows macOS/Windows, instaladores, identidade de build, checksums, scripts de assinatura | Certificados e validação de distribuição pública pendentes |
| Website/docs | Landing bilíngue, guias de configuração e validação | Plano original e alguns textos ficaram para trás em relação à implementação |
| Outras plataformas/apoios | Estados “em breve” e aba informativa | YouTube, Kick, Facebook, Super Chats, Bits e inscrições não estão integrados |

Os comandos “salva isso como nota/ideias” salvam a resposta atual. Eles não registram automaticamente tudo que o streamer fala. Ideias salvas explicitamente já existem; geração automática de ideias permanece fora do candidato.

## Backlog prioritário: experiência principal

P = pequeno, M = médio, G = grande. Esforço relativo, não prazo; áudio nativo e múltiplos sistemas aumentam a incerteza.

| ID | Melhoria | Benefício e critério de aceite | Esforço |
| --- | --- | --- | --- |
| 01 | Estados claros de escuta, transcrição, análise, fala e pausa | O streamer sabe quando pode falar; todo sucesso, erro e cancelamento termina em estado recuperável | M |
| 02 | Atalhos globais configuráveis | Falar enquanto segura uma tecla, silenciar e mostrar/ocultar Rocky sem sair do jogo; detectar conflito de atalhos | G |
| 03 | Detectar fim da fala | Enviar o comando após silêncio configurável, com limite máximo; comparar latência e falsos cortes com o bloco atual de oito segundos | G |
| 04 | Histórico curto de perguntas e respostas | Ver as últimas interações, copiar, repetir e salvar uma resposta específica; manter limites de contexto e limpar ao encerrar | M |
| 05 | Continuidade da conversa | “Explica melhor” e “resume isso” usam a interação anterior; separar histórico do streamer de mensagens não confiáveis do chat | M |
| 06 | Prioridade ao comando direto | Uma pergunta explícita cancela/substitui análise automática; impedir respostas antigas e fila de fala obsoleta | M |
| 07 | Comandos locais adicionais | “Pare”, “repita”, “mais curto”, “próxima”; parada por atalho funciona mesmo quando o microfone está pausado | M |
| 08 | Nota/ideia livre por texto e ditado | “Anota: fazer uma live sobre X” salva conteúdo fornecido, sem exigir resposta da IA ou mensagens de chat | M |
| 09 | Confirmação discreta e desfazer | Salvar sem trocar obrigatoriamente de aba; indicar destino, evitar duplicação acidental e permitir desfazer | P |
| 10 | Perguntar com Enter e manter rascunho por erro | Enter envia, Shift+Enter quebra linha; limpar após sucesso, oferecer “repetir última pergunta” explicitamente | P |
| 11 | Configuração inicial orientada | Build oficial oferece “Conectar Twitch”; Client ID/endpoints ficam em opções avançadas; cada erro aponta uma ação | M |
| 12 | Escolha guiada de modelo | Listar modelos disponíveis no provedor e validar o selecionado; separar opção local e nuvem sem trocar silenciosamente | M |
| 13 | Teste rápido antes da live | Em uma tela verificar Twitch, IA e áudio opcional, com resultado e ação corretiva; não bloquear uso por texto por erro de voz | M |
| 14 | Guia de áudio/OBS no aplicativo | Teste de gravação orientado por sistema; seleção de saída quando suportada; usuário confirma onde ouviu o Rocky | M–G |
| 15 | Janela que lembra o usuário | Persistir tamanho, posição e estado; recuperar janela ao remover monitor; mostrar/ocultar pelo tray/menu bar | M |
| 16 | Navegação focada no que funciona | Ocultar ou agrupar áreas “em breve”; manter Conversa, Notas, Ideias e configurações acessíveis | P |
| 17 | Legibilidade e teclado | Tamanho de fonte ajustável, foco visível, rótulos acessíveis e conteúdo utilizável em escalas diferentes | M |

Para 03, whisper.cpp oferece primitivas e exemplos de detecção de atividade de voz; a integração em tempo real e o ajuste contra ruído ainda exigem trabalho no Rocky. [Exemplo oficial](https://github.com/ggml-org/whisper.cpp/blob/master/examples/vad-speech-segments/speech.cpp).

Para 14, o OBS documenta captura de áudio por aplicativo no Windows e limitações de compatibilidade. A seleção de saída do Rocky, sozinha, não comprova que sua voz ficou fora da transmissão. [Guia oficial do OBS](https://obsproject.com/kb/application-audio-capture-guide).

## Backlog seguinte: utilidade durante e depois da live

| ID | Melhoria | Benefício e critério de aceite | Esforço |
| --- | --- | --- | --- |
| 18 | Agrupar perguntas repetidas | Mostrar temas com fontes e contagens calculadas; não confundir número de mensagens com pessoas | G |
| 19 | Fila “pendente/respondida/ignorada” | O streamer marca o que já abordou; sugestões não voltam sem nova evidência relevante | M |
| 20 | Botões de perguntas rápidas | “Dúvidas principais”, “O que perdi?” e “Ideias do chat”; indicar o intervalo realmente coberto | P |
| 21 | Contexto de canal e sessão | Tema, jogo e preferências informados pelo streamer ajudam a interpretar o chat; visíveis/editáveis | M |
| 22 | Filtros de contexto | Ignorar bots, comandos e spam repetido; limites por autor; explicar que há filtragem da amostra | M |
| 23 | Perfil de intervenção | Sob demanda, discreto e proativo; controlar tamanho da resposta e frequência; priorizar silêncio quando desejado | M |
| 24 | Sessões com começo/fim | Organizar notas e ideias por transmissão, canal e horário relativo; migração preserva registros antigos | G |
| 25 | Busca e organização | Buscar conteúdo, filtrar sessão/categoria, marcar ideia como realizada; não depender de rolagem longa | M |
| 26 | Resumo pós-live limitado ao material salvo | Ao encerrar, reunir notas, ideias e pendências; nomear como resumo dos registros, sem alegar cobertura da live inteira | M |
| 27 | Marcador de momento | “Marca esse momento” salva horário relativo e observação; não promete criar um clipe ou vídeo | M |
| 28 | Pulso com referência real | Trocar barra arbitrária por tendência temporal e origem das métricas; esconder valores indisponíveis | M |
| 29 | Histórico de consumo e modo econômico | Registrar latência, tentativas e uso informado; limitar análises automáticas; custo monetário só com preço verificável e escopo explícito | M |
| 30 | Diagnóstico exportável | Relatório local com versão e falhas técnicas, sem chave/chat por padrão, revisável antes de compartilhar | M |
| 31 | Backup e importação | Restaurar notas/ideias com validação e tratamento de duplicatas; testar recuperação em outra instalação | M |
| 32 | Lembrar conexão Twitch, opcional | Guardar credencial no cofre com renovação, revogação e “esquecer conta”; alternativa atual continua disponível | G |
| 33 | Aviso de nova versão | Consultar canal oficial, mostrar notas e abrir download; evitar atualização automática durante uma live | M |
| 34 | Demonstração sem conta ou IA configurada | Replay claramente identificado permite entender perguntas, evidências e notas antes de configurar serviços | M |
| 35 | Resposta progressiva | Mostrar progresso útil e, se adotado streaming, exibir/falar apenas conteúdo validado; respeitar cancelamento | G |

## Evoluções posteriores

- Integração YouTube e Super Chats: requer conector, autenticação, limites e fila de apoios com deduplicação.
- Bits/inscrições Twitch: tratar tipos e permissões próprios, com prioridade configurável.
- OBS/Stream Deck: controles e marcadores explícitos, após estabilizar os comandos locais.
- Memória longa da live: resumos incrementais com evidências, retenção escolhida e custo limitado.
- Interromper Rocky falando por cima dele: depende de captura simultânea e supressão da própria voz; o atalho vem antes.
- Clipes automáticos, sincronização entre dispositivos, aplicativo mobile, plugins e novas redes: fora do primeiro MVP.

## Ordem recomendada

1. **Conversa confiável:** 01, 06, 10, 04 e 05.
2. **Uso sem tirar as mãos do jogo:** 02, 07, 08 e 09; avaliar 03 em um experimento técnico separado.
3. **Começar e operar com menos esforço:** 11–17, priorizando primeiro uso, áudio e janela.
4. **Acompanhar o que importa:** 18–23 após sessões reais mostrarem volume suficiente de chat.
5. **Valor depois da live:** 24–28 e 31.
6. **Operação e expansão:** 29, 30 e 32–35 conforme feedback.

Não é necessário implementar as 35 propostas antes de testar o MVP. Meu primeiro recorte seria 01, 02, 04, 05, 06, 08, 09, 10, 11, 13, 14, 15 e 16, com o trabalho de voz 03 condicionado a medição. Reduzir esse recorte se os testes com streamers mostrarem que uma etapa ainda não é necessária.

## Critérios para avaliar a experiência

Metas a definir/medir em hardware identificado, não resultados já alcançados:

- Tempo de instalação até a primeira resposta útil, com quantidade de passos que exigem ajuda.
- Tempo do fim da fala até a resposta; separar captura, transcrição, IA e síntese.
- Quantidade de comandos entendidos, repetidos e abandonados por sessão.
- Quantidade de intervenções consideradas úteis e interrupções indesejadas.
- Sucesso ao salvar, reencontrar e exportar notas/ideias após reiniciar.
- Uso de CPU/memória do Rocky e impacto nos frames do OBS, distinguindo o processo da IA local.
- Recuperação de queda de rede, erro do provedor e troca de dispositivo de áudio.

Coletar inicialmente por observação e registros locais com consentimento, sem acrescentar telemetria obrigatória.

## Base técnica e cuidado com o escopo

- apps/desktop compõe serviços e controla janela/empacotamento.
- shared/core contém contratos, modelos e construção de prompts.
- shared/data implementa Twitch, IA e SQLite.
- shared/ui concentra componentes e estados. RockyWindow já coordena várias rotinas de sessão, IA, voz e notas; extrair um coordenador de sessão ao implementar 01/06 ajuda a testar transições.
- platform/desktop isola cofre, áudio, arquivos e preferências.
- apps/web e docs precisam comunicar capacidades reais. Atualizar o plano histórico não significa implementar tudo que ele descreve.
- Ideias hoje usam a tag IDEIA no repositório de notas; tipo e sessão explícitos ficam adequados quando entrarmos em 24/25.
- O TimelineContent antigo ainda existe, mas a aba real de ideias usa NotesContent. Remover código sem uso como manutenção pequena, sem confundir com nova funcionalidade.

A base anterior passou em 132 testes. Nesta revisão, a alteração de largura passou na compilação desktop; a suíte completa não foi repetida para duas constantes de layout. Este documento não substitui teste manual macOS/Windows, cofre nativo, sessão longa com Twitch/voz/OBS nem assinatura/notarização dos instaladores.
