# Homologação de atualizações e upgrades

Execute com pacotes assinados de teste e backup recente dos dados locais.

## Descoberta e download

- [ ] Detectar a versão estável ou alpha conforme o canal atual.
- [ ] Ignorar release em rascunho, tag inválida e versão anterior.
- [ ] Selecionar DMG correto em Mac Apple Silicon e Intel.
- [ ] Selecionar MSI correto em Windows x64 e ARM64.
- [ ] Rejeitar sistema, arquitetura ou artefato sem suporte.
- [ ] Baixar um pacote real e acompanhar o progresso.
- [ ] Cancelar antes do início e durante o download.
- [ ] Tentar novamente depois de cada cancelamento.
- [ ] Desligar a rede durante checksum e durante o pacote.
- [ ] Recuperar depois de restaurar a conexão.
- [ ] Confirmar que downloads parciais são removidos.
- [ ] Rejeitar redirecionamento para origem não autorizada.

## Integridade e instalação

- [ ] Rejeitar checksum ausente, duplicado ou malformado.
- [ ] Rejeitar pacote truncado, maior que o anunciado ou alterado.
- [ ] Confirmar o hash do pacote pronto antes de abrir.
- [ ] Bloquear a instalação enquanto uma live estiver ativa.
- [ ] Abrir o instalador somente após confirmação explícita.
- [ ] Cancelar o instalador sem remover a versão atual.
- [ ] Simular falha ao abrir e tentar novamente.
- [ ] Fechar o Rocky e concluir a instalação normalmente.
- [ ] Cancelar a instalação no fluxo nativo do sistema.
- [ ] Confirmar que o aplicativo anterior continua abrindo.
- [ ] Verificar assinatura, identidade do publicador e avisos do SO.

## Upgrade e recuperação

- [ ] Instalar sobre a versão anterior em macOS e Windows.
- [ ] Reabrir e conferir versão, commit e arquitetura esperados.
- [ ] Preservar notas, ideias, momentos e histórico persistente.
- [ ] Preservar configurações de agente, voz e plataformas.
- [ ] Preservar credenciais no Keychain ou DPAPI.
- [ ] Executar e validar todas as migrações do SQLite.
- [ ] Simular falha de migração sem apagar o banco original.
- [ ] Restaurar um backup criado antes do upgrade.
- [ ] Repetir upgrade com o aplicativo aberto e fechado.
- [ ] Confirmar que downgrade incompatível é recusado com orientação.
