const portuguese = [
  {
    "tag": "SUA IA",
    "title": "Escolha onde processar",
    "text": "Use Ollama no computador ou sua chave de OpenAI/OpenRouter. Provedores remotos recebem o pedido e a amostra de chat; custos e limites dependem da sua conta.",
    "chips": [
      "Ollama",
      "OpenAI API",
      "OpenRouter"
    ]
  },
  {
    "tag": "SUA VOZ",
    "title": "Escolha a voz",
    "text": "Escolha voz do sistema ou ElevenLabs e ajuste a velocidade. ElevenLabs recebe o texto falado e usa os créditos da sua conta. A transcrição usa whisper.cpp local, com preparação automática no Mac com Homebrew ou manual no Windows.",
    "chips": [
      "Voz do macOS",
      "Voz do Windows",
      "ElevenLabs",
      "whisper.cpp"
    ]
  }
];

const english = [
  {
    "tag": "YOUR AI",
    "title": "Choose where processing happens",
    "text": "Use Ollama on your computer or your OpenAI/OpenRouter key. Remote providers receive the request and chat sample; costs and limits depend on your account.",
    "chips": ["Ollama", "OpenAI API", "OpenRouter"]
  },
  {
    "tag": "YOUR VOICE",
    "title": "Choose the voice",
    "text": "Choose a system voice or ElevenLabs and adjust the speed. ElevenLabs receives spoken text and uses your account credits. Transcription uses local whisper.cpp, with automatic setup on Mac through Homebrew or manual setup on Windows.",
    "chips": ["macOS voice", "Windows voice", "ElevenLabs", "whisper.cpp"]
  }
];

export const getLocalCards = locale => locale === "pt-BR" ? portuguese : english;
