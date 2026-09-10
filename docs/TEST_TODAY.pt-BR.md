# Candidato de teste — 1.0.10-alpha.1

Escopo: chat da própria conta Twitch, sugestões de IA por texto e notas com fontes. A IA pode usar Ollama, OpenAI ou modelos gratuitos pelo OpenRouter. Voz é opcional e começa desativada em instalações novas. Este candidato ainda precisa de validação em live real e OBS antes de lançamento amplo.

## Preparar

1. Instale o pacote **1.0.10**. Não use um pacote de uma execução anterior.
2. Abra **Configurações → Plataformas**. Este pacote não inclui um Client ID oficial. Registre um aplicativo público na Twitch usando sua conta e copie o Client ID, conforme [configuração Twitch](TWITCH.pt-BR.md). Não é necessário criar outra conta de streamer nem informar Client Secret.
3. Conecte e autorize a mesma conta em que abrirá a live. O Rocky acompanha novas mensagens desse canal; não lê histórico anterior nem mede espectadores.
4. Em **IA**, escolha Ollama, OpenAI ou OpenRouter. Para testar sem custo por tokens, mantenha `openrouter/free`. **Testar conexão** faz uma geração curta com mensagens fictícias. Conclua o guia sem configurar microfone.

## Teste rápido antes da live

- [ ] Enviar uma mensagem no chat e vê-la no Rocky.
- [ ] Digitar uma pergunta, receber uma sugestão e abrir **Ver fontes**. Comparar a fonte com o chat.
- [ ] Cancelar uma análise em andamento e enviar outra pergunta.
- [ ] Salvar a sugestão, editar a nota, reiniciar o app e conferir texto, data com fuso e fontes. Exportar Markdown.
- [ ] Testar uma resposta longa, alternar para o modo compacto e abrir configurações a partir dele.
- [ ] Conferir mensagens/minuto durante uma rajada e após um minuto sem mensagens.
- [ ] Desligar a rede por 15 e 60 segundos; restaurar e confirmar que novas mensagens chegam. A sugestão já recebida deve permanecer durante a recuperação. Mensagens enviadas durante a queda podem não ser recuperadas pela Twitch.
- [ ] Se usar voz, ativar a leitura nas configurações, testar e silenciar nos modos completo e compacto.

## Sessão e registro

Reserve duas horas para a sessão com OBS e siga o [protocolo completo](STREAMER_TEST.pt-BR.md), incluindo instalação limpa no macOS e Windows. Use uma gravação local para conferir captura da janela e roteamento de áudio antes da transmissão.

Registre versão, commit, sistema, instalador, resultado e evidência de cada cenário. Build, testes com serviços simulados e empacotamento não comprovam OAuth real, qualidade do modelo, recuperação na rede do streamer ou áudio no OBS. Esses resultados continuam pendentes até a execução humana.
