# Homologação de segurança e privacidade

Execute com credenciais descartáveis e contas de sistema criadas para o teste.

## Endpoints e transporte

- [ ] Aceitar HTTPS remoto válido para cada provedor.
- [ ] Aceitar HTTP somente para Ollama em loopback.
- [ ] Rejeitar HTTP remoto, `file:`, host ausente e porta indevida.
- [ ] Rejeitar endpoint com usuário, senha, query ou fragmento.
- [ ] Rejeitar redirecionamento de voz ou update para origem não autorizada.
- [ ] Confirmar que OAuth usa estado e PKCE quando aplicável.
- [ ] Confirmar que callbacks locais escutam somente em loopback.

## Cofre de credenciais

- [ ] Salvar, ler, editar e apagar uma chave no Keychain do macOS.
- [ ] Salvar, ler, editar e apagar uma chave usando DPAPI no Windows.
- [ ] Migrar uma credencial antiga e remover o valor em plaintext.
- [ ] Simular cofre indisponível sem persistir a chave em preferências.
