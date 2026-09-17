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
- [ ] Reiniciar após apagar e confirmar que a credencial não retorna.
- [ ] Trocar provedor ou origem e não reutilizar a chave anterior.
- [ ] Entrar com outro usuário do sistema e confirmar isolamento.
- [ ] Fechar o Rocky e confirmar remoção dos tokens temporários.

## Arquivos e diagnósticos

- [ ] Inspecionar preferências, SQLite e pasta de dados por chaves.
- [ ] Exportar backup e confirmar ausência de credenciais.
- [ ] Exportar diagnóstico e confirmar ausência de chaves e tokens.
- [ ] Confirmar que diagnóstico não contém chat, canal ou caminhos locais.
- [ ] Inspecionar logs após falha de autenticação e de rede.
- [ ] Verificar permissões da pasta, banco, backups e arquivos DPAPI.
- [ ] Confirmar que arquivos temporários são removidos após erro.

## Conteúdo não confiável

- [ ] Enviar no chat pedidos para ignorar regras e revelar chaves.
- [ ] Enviar comandos de shell, URLs, Markdown e texto semelhante a JSON.
- [ ] Confirmar que chat nunca muda configurações ou executa ações locais.
- [ ] Confirmar que pedidos do streamer permanecem separados do chat.
- [ ] Rejeitar IDs de fontes inventados pela resposta da IA.
- [ ] Limitar tamanho, quantidade e intervalo das mensagens enviadas à IA.
- [ ] Confirmar que erros do provedor não exibem seu payload privado.
- [ ] Testar nomes, mensagens e arquivos com caracteres especiais.
- [ ] Repetir os testes com telemetria desativada e rede desconectada.
