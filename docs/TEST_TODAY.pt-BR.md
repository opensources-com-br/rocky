# Candidato de validação — 1.0.11-alpha.1

Escopo: Twitch, IA, perguntas por texto, voz opcional e notas com fontes. Gere o candidato conforme [preparação de lançamento](RELEASE_PREPARATION.md); este documento não afirma que a versão já foi publicada ou homologada.

1. Instale em máquina limpa. Em **Dados**, registre versão, commit e arquitetura; compare com `BUILDINFO` e checksums do pacote.
2. Em **Plataformas**, use o Client ID incluído quando disponível. Só registre um cliente público próprio se o campo estiver vazio. Autorize a conta do canal da live.
3. Configure a IA, teste uma geração e salve explicitamente a configuração/chave. Feche e reabra: confira recuperação pelo cofre. Remova a chave e confira que não volta. Troque provedor/endereço: a chave anterior não deve acompanhar a troca.
4. Conclua o onboarding sem configurar voz. Envie uma pergunta textual, cancele uma análise e envie outra; teste falha de rede/cota e a mensagem de recuperação.
5. Abra fontes, salve uma nota, edite, reinicie e exporte. Confira data, mensagem, autor, canal e sessão. Teste exportação para um destino indisponível: deve haver erro recuperável.
6. Opcionalmente prepare voz. No Mac, teste cancelamento do download e nova tentativa; no Windows, selecione whisper-cli/modelo. Use o teste de conversa para validar microfone, idioma e TTS.
7. Durante a live diga “Rocky” (ou o nome configurado) e um pedido. Interrompa a resposta com Silenciar/Próxima e faça outro pedido. Teste PT-BR e EN, nome personalizado e ausência de reação a palavras apenas parecidas.
8. Use o modo compacto, abra configurações e volte. No Mac o volume próprio está desabilitado; confira a saída do sistema. Fale apenas durante os oito segundos de captura; registre comandos cortados ou perdidos nas transições.
9. Desligue a rede por 15/60 s, restaure e confira novas mensagens, aviso de lacuna e audiência indisponível quando desatualizada. Desconectar deve cancelar a recuperação.
10. Exporte um backup e teste exclusão de notas: cancelar preserva; confirmar apaga. Redefinir configurações preserva notas/exportações; remover modelo preserva modelos externos.

Execute depois o [protocolo de streamers](STREAMER_TEST.pt-BR.md): duas pessoas, três sessões, uma de duas horas, OBS em macOS e Windows. Registre memória, latência, interrupções e qualidade das fontes. Os tokens exibidos são parciais, não o faturamento do provedor.

Build/testes/DMG não comprovam captura real, OAuth, cofre nativo, assinatura, upgrade ou ausência de áudio no OBS. Registre os resultados humanos antes de promover o candidato.
