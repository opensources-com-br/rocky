# Candidato de validação — 1.0.11-alpha.1

Escopo: Twitch, Kick, YouTube, Facebook, TikTok LIVE, IA, ações rápidas, voz do sistema/ElevenLabs, perguntas agrupadas, notas, ideias, momentos, resumo local e atualização assistida. Gere o candidato conforme [preparação de lançamento](RELEASE_PREPARATION.md); este documento não afirma que a versão já foi publicada ou homologada.

1. Instale em máquina limpa. Em **Dados**, registre versão, commit e arquitetura; compare com `BUILDINFO` e checksums do pacote.
2. Em **Plataformas**, use o Client ID incluído quando disponível. Só registre um cliente público próprio se o campo estiver vazio. Autorize a conta do canal da live.
3. Configure a IA, teste uma geração e salve explicitamente a configuração/chave. Feche e reabra: confira recuperação pelo cofre. Remova a chave e confira que não volta. Troque provedor/endereço: a chave anterior não deve acompanhar a troca.
4. Conclua o onboarding sem configurar voz. Use uma ação rápida, cancele a análise e use outra; teste falha de rede/cota e a mensagem de recuperação.
5. Abra fontes, salve uma nota, edite, reinicie e exporte. Confira data, mensagem, autor, canal e sessão. Teste exportação para um destino indisponível: deve haver erro recuperável.
6. Opcionalmente prepare voz. No Mac, teste cancelamento do download e nova tentativa; no Windows, selecione whisper-cli/modelo. Use o teste de conversa para validar microfone, idioma e TTS.
7. Durante a live diga “Rocky” (ou o nome configurado) e um pedido. Interrompa a resposta com Silenciar/Próxima e faça outro pedido. Teste PT-BR e EN, nome personalizado e ausência de reação a palavras apenas parecidas.
8. Use o modo compacto, abra configurações e volte. Na voz local do Mac, confira o volume do sistema; ElevenLabs permite volume no app. Calibre o ruído e fale durante a captura; registre cortes nas transições e teste o atalho para interromper a reprodução.
9. Desligue a rede por 15/60 s, restaure e confira novas mensagens, aviso de lacuna e audiência indisponível quando desatualizada. Desconectar deve cancelar a recuperação.
10. Exporte um backup e teste exclusão de notas: cancelar preserva; confirmar apaga. Redefinir configurações preserva notas/exportações; remover modelo preserva modelos externos.

Execute depois o [protocolo de streamers](STREAMER_TEST.pt-BR.md): duas pessoas, três sessões, uma de duas horas, OBS em macOS e Windows. Registre memória, latência, interrupções e qualidade das fontes. Os tokens exibidos são parciais, não o faturamento do provedor.

Build/testes/DMG não comprovam captura real, OAuth, cofre nativo, assinatura, upgrade ou ausência de áudio no OBS. Registre os resultados humanos antes de promover o candidato.

## Regressão de perguntas e comandos de salvar

- Faça uma pergunta por voz; depois da resposta, confirme que o microfone volta a capturar sem clicar em Próxima.
- Repita a pergunta sem enviar novas mensagens no chat, por voz e pelas ações rápidas. O campo de pergunta digitada está oculto. Uma nova resposta deve ser gerada.
- Aguarde mais de dois minutos sem mensagens e pergunte novamente. Rocky deve responder explicando a falta de contexto recente, sem inventar mensagens.
- Com uma resposta na tela, diga “Rocky, salva isso como nota” e depois “Rocky, salva isso como ideias”. Os comandos salvam a resposta atual, sem consultar a IA novamente.
- Confira as abas Notas e Ideias, reinicie o aplicativo e confirme a persistência. Teste edição, exclusão e exportação de cada categoria.
- Sem resposta disponível, o comando de salvar deve explicar que ainda não há uma resposta para salvar.
- Confira o ícone com a letra R sobre fundo laranja, igual ao da web, na janela, no Dock do macOS e no instalador. No Windows, confira também a barra de tarefas e o atalho instalado.


## Experiência do MVP: dez melhorias

- Histórico: gere duas respostas, consulte a primeira, copie, repita, salve como ideia e desfaça. Desconecte e confirme que o histórico foi limpo e os registros salvos permaneceram.
- Continuidade: após uma resposta, peça “explica melhor” e “resume isso”; confirme que o assunto anterior foi mantido sem inventar novas mensagens.
- Prioridade: inicie análise automática lenta e envie uma pergunta direta. Somente a resposta ao pedido direto deve entrar no histórico.
- Atalhos: teste Ctrl+Shift+F8/F9/F10 com Rocky em segundo plano e durante OBS/jogo. Reconfigure, confira conflitos e teste de novo.
- Fim da fala: compare um comando curto, uma pausa no meio da frase e silêncio total. Ajuste o limiar com ruído ambiente; confirme o limite de 30 segundos com fala em andamento e oito segundos sem detecção.
- Registro livre: dite uma nota sem chat/IA, crie uma ideia por texto, confirme que a aba não mudou, desfaça e verifique SQLite ao reabrir.
- Primeiro uso: conecte Twitch, Kick, YouTube, Facebook ou TikTok LIVE; valide credenciais próprias, o webhook HTTPS público da Kick, uma Página com live ativa no Facebook e um usuário com live pública ativa no TikTok. Busque modelos, selecione um, salve e teste a IA.
- Pré-live: teste IA, voz/microfone opcionais e confira uma gravação OBS seguindo a orientação exibida.
- Janela: mova/redimensione, reinicie, teste modo compacto e configurações sem perder o tamanho principal; remova um monitor e confira recuperação.
- Falhas: interrompa rede/provedor, tente salvar com pasta de dados indisponível e confirme que rascunhos não desaparecem sem salvar.

## Fila, registros e manutenção

Use o [roteiro de organização da live](MVP_CHAT_WORKSPACE.pt-BR.md#validação-manual-desta-etapa) para testar as nove melhorias adicionais, incluindo backup e marcadores de voz.


### Verificação de ElevenLabs e conversa

- Selecione ElevenLabs em Voz, salve a chave, carregue voz/modelo e teste reprodução, velocidade e volume no macOS e Windows.
- Interrompa durante a espera da rede e durante a fala; nenhuma resposta antiga deve voltar. Teste chave inválida, quota e alternativa local desativada/ativada. Falha após iniciar áudio não deve repetir tudo com outra voz.
- Pergunte, reformule durante a análise e use “resume isso” após a resposta. Salve nota e ideia em sequência sem repetir o nome. Após 12 segundos, uma continuação sem nome deve ser ignorada.
- Use uma frase longa, pausas naturais e ruído de teclado/jogo. Confirme o início das palavras; a calibração não substitui um teste com o microfone real.
- Com alto-falantes, confirme a pausa do microfone na reprodução e a retomada depois. Interrompa pelo atalho de fala; não pressupor cancelamento acústico de eco.
