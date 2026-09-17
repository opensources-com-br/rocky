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
