const content = {
  "group": "Recursos",
  "lead": "A aba Conversa reúne o chat recebido das conexões ativas da Twitch, Kick, YouTube, Facebook e TikTok LIVE. A fila de perguntas organiza uma parte desse conteúdo.",
  "blocks": [
    {
      "type": "h2",
      "text": "Perguntas agrupadas"
    },
    {
      "type": "para",
      "text": "Perguntas detectadas por regras de texto são agrupadas por semelhança e salvas por sessão Rocky. Use a fila para consultar fontes e marcar pendentes ou respondidas. A detecção pode deixar perguntas de fora; não garante equivalência semântica nem identifica automaticamente tudo que foi respondido em voz alta."
    },
    {
      "type": "h2",
      "text": "Filtros do contexto"
    },
    {
      "type": "list",
      "items": [
        "Comandos iniciados por ! ou / são ignorados por padrão.",
        "Nightbot, StreamElements, Streamlabs e Moobot estão na lista inicial de bots; a lista é editável.",
        "Repetições do mesmo autor e rajadas acima de dez mensagens em trinta segundos podem ser excluídas do contexto, além de longas repetições de caracteres.",
        "Filtros afetam a IA e a fila de perguntas. O chat exibido permanece intacto. Não importam a lista de usuários silenciados da plataforma."
      ]
    },
    {
      "type": "h2",
      "text": "Atalhos de pergunta"
    },
    {
      "type": "para",
      "text": "Dúvidas principais, O que perdi? e Ideias do chat enviam pedidos prontos à IA. O resultado usa a mesma amostra recente; não reconstrói períodos ausentes nem a live inteira."
    },
    {
      "type": "h2",
      "text": "Pulso"
    },
    {
      "type": "para",
      "text": "A faixa superior conta somente as plataformas online e mostra a audiência de cada conexão em formato compacto. O rodapé soma as audiências disponíveis e identifica o valor como total. A aba Pulso mostra as tendências e os valores por plataforma; quedas de conexão ou ausência de dados limitam a leitura."
    }
  ]
};

export default content;
