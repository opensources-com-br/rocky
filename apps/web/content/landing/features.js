const portuguese = [
  {
    "tag": "VOZ",
    "title": "Pergunte por voz ou use atalhos",
    "text": "Use transcrição local com whisper.cpp e respostas pela voz do sistema ou ElevenLabs, ou use as ações rápidas sem microfone. O campo de pergunta digitada está oculto no layout atual."
  },
  {
    "tag": "NOTAS",
    "title": "Guarde o que importa",
    "text": "Salve respostas e ditados como notas, com edição, busca e exportação Markdown."
  },
  {
    "tag": "IDEIAS",
    "title": "Organize suas pautas",
    "text": "Peça ideias à IA, salve as que escolher e marque as realizadas. Filtre os registros por live."
  },
  {
    "tag": "MOMENTOS",
    "title": "Marque um momento",
    "text": "Diga “Rocky, marca esse momento” para guardar uma referência de tempo da sessão, sem gravar ou cortar vídeo."
  },
  {
    "tag": "PULSO",
    "title": "Acompanhe o ritmo",
    "text": "Veja espectadores e mensagens por minuto da Twitch, Kick, YouTube, Facebook ou TikTok LIVE, com tendências calculadas das amostras recebidas."
  },
  {
    "tag": "RESUMO",
    "title": "Revise os registros",
    "text": "Ao desconectar ou fechar, consulte o resumo local das notas, ideias, momentos e perguntas pendentes salvas."
  }
];

const english = [
  {
    "tag": "VOICE",
    "title": "Ask by voice or use shortcuts",
    "text": "Use local whisper.cpp transcription and system or ElevenLabs speech, or use quick actions without a microphone. The typed question field is hidden in the current layout."
  },
  {
    "tag": "NOTES",
    "title": "Keep what matters",
    "text": "Save answers and dictation as notes, with editing, search, and Markdown export."
  },
  {
    "tag": "IDEAS",
    "title": "Organize your topics",
    "text": "Ask AI for ideas, save the ones you choose, and mark completed ideas. Filter records by stream."
  },
  {
    "tag": "MOMENTS",
    "title": "Mark a moment",
    "text": "Say “Rocky, mark this moment” to save a session time reference without recording or cutting video."
  },
  {
    "tag": "PULSE",
    "title": "Track the pace",
    "text": "See viewers and messages per minute from Twitch, Kick, YouTube, Facebook, or TikTok LIVE, with trends calculated from received samples."
  },
  {
    "tag": "SUMMARY",
    "title": "Review your records",
    "text": "When you disconnect or close the app, review a local summary of saved notes, ideas, moments, and pending questions."
  }
];

export const getFeatures = locale => locale === "pt-BR" ? portuguese : english;
