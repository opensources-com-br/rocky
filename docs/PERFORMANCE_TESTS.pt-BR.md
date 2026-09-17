# Fase 14 — desempenho e estabilidade

Execute com o mesmo candidato no macOS e Windows, sem reiniciar o Rocky durante cada sessão.
Registre versão, commit, SO, arquitetura, resolução, escala, plataforma, OBS e provedor de IA.

## Carga prolongada

- Faça uma live real de duas horas e mantenha uma segunda conta enviando mensagens.
- Registre RAM e CPU em 0, 30, 60, 90 e 120 minutos pelo monitor do sistema.
- Envie tráfego normal e uma rajada de pelo menos 20 mensagens em um minuto.
- Gere sugestões manuais e automáticas durante toda a sessão.
- Use a voz a cada 30 minutos e interrompa ao menos uma captura e uma reprodução.
- Salve, edite, exclua e exporte registros depois de 30, 60 e 120 minutos.
- Alterne todas as abas; redimensione, fixe, minimize e use o modo compacto.
- Troque cenas no OBS e mantenha o jogo ou aplicativo principal em primeiro plano.
- Confirme continuamente que mensagens novas chegam em ordem e sem duplicação.

## Critérios de aprovação

- A interface permanece responsiva e nenhuma reinicialização é necessária.
- O histórico mantém somente as 1.000 mensagens mais recentes por plataforma.
- As métricas mantêm no máximo 60 buckets por minuto e 120 amostras de pulso.
- O histórico de conversa da IA permanece limitado a 30 respostas.
- A RAM estabiliza após 30 minutos e cresce no máximo 300 MB até o encerramento.
- A CPU volta ao patamar de repouso depois de rajadas, IA, voz e exportação.
- Toda operação iniciada termina, falha de forma recuperável ou pode ser cancelada.
- Registros salvos sobrevivem à reinicialização e o Markdown exportado abre.
- Nenhuma ação antiga reaparece após cancelamento, troca de sessão ou reconexão.
- O OBS não perde a janela e não passa a capturar áudio ou Settings sem intenção.

## Recuperação e evidência

- Aos 45 minutos, desligue a rede por 15 segundos; aos 75, por 60 segundos.
- Depois de cada retorno, envie uma mensagem única e confirme uma única entrega.
- Durante uma análise, desconecte a plataforma e confirme o cancelamento seguro.
- Feche e reabra o OBS sem encerrar o Rocky; refaça a captura da janela.
- Após duas horas, reinicie o Rocky e valide os registros persistidos.
- Anexe capturas do processo em cada medição e do histórico ao fim da sessão.
- Guarde uma gravação curta do começo, do meio e do fim, sem expor chaves.
- Registre tempos de recuperação, latência percebida e qualquer travamento visual.
- Abra uma issue para cada falha com horário, ação, log e passos de reprodução.
- Marque separadamente macOS e Windows como passou, falhou ou não executado.
