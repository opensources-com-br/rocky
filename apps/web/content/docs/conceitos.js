const content = {
  "group": "Começando",
  "lead": "A conversa, a fila de perguntas e os registros têm papéis diferentes no Rocky.",
  "blocks": [
    {
      "type": "h2",
      "text": "Durante a sessão"
    },
    {
      "type": "cards",
      "items": [
        {
          "key": "chat",
          "text": "Novas mensagens da Twitch, Kick ou YouTube recebidas enquanto conectado. Não é um arquivo completo da transmissão."
        },
        {
          "key": "sugestão",
          "text": "Resposta da IA baseada na amostra recente, que pode ser salva por você."
        },
        {
          "key": "histórico",
          "text": "Até 30 respostas temporárias da sessão; os últimos quatro pares ajudam a continuar a conversa."
        },
        {
          "key": "perguntas",
          "text": "Grupos detectados por regras de texto, com contagem de mensagens e estado pendente ou respondido."
        },
        {
          "key": "pulso",
          "text": "Métricas e tendências das amostras de espectadores e mensagens da plataforma conectada."
        }
      ]
    },
    {
      "type": "h2",
      "text": "O que persiste"
    },
    {
      "type": "para",
      "text": "Notas, ideias, perguntas com evidências, momentos e resumos ficam em SQLite. O histórico de respostas é temporário e limpa ao desconectar. Uma pergunta marcada como respondida reflete uma ação do usuário, não uma verificação automática da fala."
    },
    {
      "type": "h2",
      "text": "Tempo da sessão"
    },
    {
      "type": "para",
      "text": "Momentos e registros associados à live usam o tempo desde o início da conexão. Esse tempo não é necessariamente o timestamp do vídeo publicado."
    }
  ]
};

export default content;
