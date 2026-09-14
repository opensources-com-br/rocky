const portuguese = [
  {
    "n": "01",
    "title": "Acompanha sua live",
    "text": "Recebe novas mensagens da Twitch, Kick, YouTube, Facebook ou TikTok LIVE e agrupa perguntas. A Kick exige um webhook HTTPS público; o TikTok usa uma conexão WebCast não oficial."
  },
  {
    "n": "02",
    "title": "Ajuda a consultar o chat",
    "text": "Use “Dúvidas principais”, “O que perdi?” e “Ideias do chat”. A análise considera uma amostra dos últimos dois minutos."
  },
  {
    "n": "03",
    "title": "Conversa com você",
    "text": "Com voz configurada, diga “Rocky” e faça seu pedido. Use fones e teste o OBS: a captura de áudio do desktop pode incluir as respostas."
  }
];

const english = [
  {
    "n": "01",
    "title": "Follows your stream",
    "text": "Receives new Twitch, Kick, YouTube, Facebook, or TikTok LIVE messages and groups questions. Kick requires a public HTTPS webhook; TikTok uses an unofficial WebCast connection."
  },
  {
    "n": "02",
    "title": "Helps you read the chat",
    "text": "Use “Top questions,” “What did I miss?” and “Chat ideas.” Analysis considers a sample from the last two minutes."
  },
  {
    "n": "03",
    "title": "Talks with you",
    "text": "With voice configured, say “Rocky” and make your request. Use headphones and test OBS: desktop audio capture may include the answers."
  }
];

export const getSteps = locale => locale === "pt-BR" ? portuguese : english;
