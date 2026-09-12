# Organização da live e assistência

- **Perguntas:** a conversa abre a fila de pendentes/respondidas, com fontes e contagem. Perguntas de texto semelhante são agrupadas por palavras, sem chamada de IA. Marque respostas manualmente ou reabra. O seletor permite consultar lives anteriores. Limite de 100 grupos por conexão/live; não é uma transcrição completa.
- **Ações rápidas:** “Dúvidas principais”, “O que perdi?” e “Ideias do chat” consultam a amostra recente recebida. Não recuperam mensagens de antes da conexão ou lacunas da Twitch.
- **Filtros em IA:** bots por nome, comandos iniciados em `!`/`/`, repetições por autor e rajadas acima de dez mensagens por autor na amostra. Podem ser ajustados. Afetam o contexto da IA e a fila, preservando o chat exibido. Não são moderação na Twitch.
- **Perfis:** sob demanda desliga análises automáticas; discreto aguarda cinco minutos entre análises; proativo, dois minutos. A primeira análise pode ocorrer assim que houver contexto. Pedidos diretos continuam prioritários.
- **Notas e ideias:** busca por texto/tag, filtro por live e marcação de ideias realizadas. Registros antigos aparecem em “Sem live”. Exportar Markdown exporta a categoria inteira, mesmo com busca ativa.
- **Encerramento:** desconectar ou fechar salva um resumo local com notas, ideias, momentos e perguntas pendentes. Ele reaparece em Notas. Não é um resumo de toda a transmissão. Uma falha de salvamento impede o encerramento normal para permitir nova tentativa.
- **Momento por voz:** “Rocky, marca esse momento” ou “Rocky, marca esse momento: vitória”. Salva em Notas com o tempo desde a conexão do Rocky; não cria clipe nem usa o início oficial da transmissão.
- **Pulso:** curvas de mensagens/minuto e espectadores medidos, amostradas a cada cinco segundos, até 120 amostras. Sem audiência disponível, não inventa valores; lacunas de conexão interrompem a curva. O histórico reinicia a cada conexão.
- **Dados:** backup JSON/importação com confirmação, diagnóstico com prévia e exportação local, consulta de releases oficiais e opção de aviso na inicialização. Veja [privacidade](PRIVACY.md).

## Validação manual desta etapa

1. Conecte a Twitch e envie perguntas semelhantes de usuários diferentes, um comando e uma mensagem de bot configurado. Compare chat, fila e contexto da IA.
2. Use as três ações rápidas e faça um pedido direto durante análise automática.
3. Marque uma pergunta respondida, salve uma ideia, marque realizada, busque e filtre por live.
4. Dite um marcador com título; confira seu tempo. Encerre e confira resumo e pendências.
5. Reconecte e confira a separação entre lives e as curvas de Pulso com dados reais.
6. Exporte backup, importe em uma instalação de teste, cancele uma importação e teste conflito de ID. Confira fontes e estado de conclusão.
7. Pré-visualize o diagnóstico e consulte atualizações. A validação de áudio real, OBS e pacote Windows continua manual.
