# Homologação de voz e microfone

Execute com o aplicativo empacotado, um microfone real e uma gravação de controle.

## Entrada e reconhecimento

- [ ] Selecionar cada microfone disponível e confirmar a troca.
- [ ] Negar a permissão, reabrir o fluxo e concedê-la.
- [ ] Transcrever com `whisper.cpp` e modelo multilíngue válidos.
- [ ] Testar executável ausente, sem permissão e incompatível.
- [ ] Testar modelo ausente, vazio, truncado e corrompido.
- [ ] Reconhecer comandos em PT-BR e inglês.
- [ ] Reconhecer Rocky e um nome personalizado.
- [ ] Ignorar palavras apenas parecidas com o nome configurado.
- [ ] Encerrar após pausas curtas, médias e longas.
- [ ] Encerrar no limite máximo mesmo com fala contínua.
- [ ] Repetir com teclado, jogo, música e ventilador.
- [ ] Confirmar que áudio temporário é removido após sucesso e erro.

## Conversa e cancelamento

- [ ] Confirmar detecção, comando, análise e resposta falada completas.
- [ ] Verificar que o microfone pausa enquanto Rocky fala.
- [ ] Confirmar que o microfone retoma depois da reprodução.
- [ ] Usar Silenciar, Próxima e o atalho durante uma resposta.
- [ ] Cancelar captura, transcrição, análise e reprodução separadamente.
- [ ] Iniciar outro comando imediatamente após cada cancelamento.
- [ ] Confirmar que nenhuma resposta cancelada aparece depois.
- [ ] Testar “Rocky, para” e os comandos equivalentes em inglês.
- [ ] Testar “explica melhor” dentro e fora da janela de continuação.

## Voz do sistema

- [ ] Listar e reproduzir vozes instaladas no macOS.
- [ ] Listar e reproduzir vozes instaladas no Windows.
- [ ] Testar os limites mínimo e máximo de velocidade.
- [ ] Testar volume no Windows e a limitação indicada no macOS.

## ElevenLabs

- [ ] Salvar uma chave válida e carregar vozes e modelos reais.
- [ ] Reproduzir com diferentes vozes, modelos, velocidades e volumes.
- [ ] Testar chave inválida, revogada e sem acesso à voz escolhida.
- [ ] Testar conta sem créditos, quota, rate limit e falha de rede.
- [ ] Cancelar antes do primeiro áudio e durante a reprodução.
- [ ] Ativar e desativar o fallback para voz local.
- [ ] Confirmar que o fallback nunca repete áudio parcialmente tocado.

## Áudio e ambiente

- [ ] Repetir com alto-falantes e com fones de ouvido.
- [ ] Confirmar que Rocky não transcreve a própria voz.
- [ ] Verificar eco, microfonia, cortes e início das palavras.
- [ ] Gravar no OBS com e sem captura do áudio do desktop.
- [ ] Repetir toda a matriz no macOS e no Windows.
