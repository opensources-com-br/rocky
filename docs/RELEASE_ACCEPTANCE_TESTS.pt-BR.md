# Fase 16 — aceite do candidato

Congele um único commit e use os pacotes produzidos por ele em toda a homologação.
Registre tag, versão lógica, versão nativa, commit, SO, arquitetura e responsável.

## Pacotes e instalação

- Gere DMG no macOS e MSI/EXE no Windows a partir de checkout limpo com JDK 17.
- Confirme que tag, `rockyVersion`, versão nativa e `BUILDINFO` correspondem.
- Confira nome, arquitetura e SHA-256 de cada instalador publicado.
- Baixe novamente os artefatos da release; não teste somente a pasta de build.
- Instale em usuário limpo e máquina sem JDK adicional.
- Abra pelo Finder ou menu Iniciar e confirme tray/menu bar e ícone corretos.
- Registre assinatura, notarização e qualquer aviso do sistema operacional.
- Feche, reabra, reinicie o computador e confirme inicialização normal.
- Desinstale e verifique atalhos, processos e entradas restantes.
- Repita separadamente em macOS e Windows nas arquiteturas publicadas.

## Jornada de ponta a ponta

- Conclua onboarding em português e inglês sem configurar voz obrigatoriamente.
- Conecte cada plataforma anunciada em uma live real e receba novas mensagens.
- Teste ao menos um provedor local e cada provedor de nuvem anunciado.
- Faça pergunta, cancele, reformule, salve nota e ideia e exporte Markdown.
- Configure microfone, transcrição e voz; interrompa e faça um novo comando.
- Execute backup, exclusão cancelada/confirmada, importação e recuperação.
- Use janela compacta, fixação, tray/menu bar, atalhos e Settings separado.
- Grave OBS nos dois sistemas sem expor chaves nem capturar áudio inesperado.
- Mantenha uma sessão de duas horas com quedas de rede de 15 e 60 segundos.
- Atualize a partir do candidato anterior e confirme configurações e registros.
- Teste instalação limpa novamente após o cenário de upgrade.

## Porta de liberação

- Exija `build`, `koverVerify`, testes Python e verificações web aprovados.
- Exija workflows de build e release verdes no commit congelado.
- Confira dependências, histórico de segredos e checksums sem alertas bloqueadores.
- Valide documentação, privacidade, limitações e notas contra o comportamento real.
- Registre três sessões reais com pelo menos dois streamers diferentes.
- Anexe logs, capturas, gravações, medições e resultado de cada matriz anterior.
- Bloqueie perda de dados, crash, conexão irrecuperável ou áudio inesperado no OBS.
- Bloqueie pacote sem runtime, identidade divergente ou checksum inválido.
- Bloqueie vazamento de chave, token, banco, transcript ou dado pessoal.
- Abra issues para falhas com versão, horário, ambiente e reprodução.
- Registre decisão final: aprovado, reprovado ou aprovado com limitações públicas.
- Publique somente os mesmos binários homologados, sem reconstrução posterior.
