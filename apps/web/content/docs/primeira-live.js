const content = {
  "group": "Começando",
  "lead": "Comece pelo assistente de primeira utilização ou pelas Configurações. A voz pode ser preparada depois; as ações rápidas de análise não exigem microfone.",
  "blocks": [
    {
      "type": "h2",
      "text": "Preparar a sessão"
    },
    {
      "type": "list",
      "items": [
        "Em Plataformas, conecte Twitch, Kick, YouTube, Facebook ou TikTok LIVE. O TikTok pede apenas o nome de usuário e uma live pública ativa; a Kick exige encaminhamento por webhook HTTPS público; YouTube e Facebook exigem credenciais de aplicativo e uma live ativa com chat.",
        "Em IA, escolha Ollama, OpenAI API, Anthropic API, Google Gemini API, xAI Grok API ou OpenRouter, informe o modelo e teste a conexão. Salve explicitamente a configuração e a chave, quando aplicável.",
        "Receba mensagens novas no canal autenticado. Use Analisar agora ou Dúvidas principais, O que perdi? e Ideias do chat na aba Conversa. O campo de pergunta digitada está oculto no layout atual.",
        "Se quiser voz, configure o whisper-cli, o modelo de transcrição e o microfone; teste a conversa de voz.",
        "Abra Antes da live para conferir a plataforma, a IA e o áudio opcional. Grave um teste no OBS para verificar o que o público ouvirá e verá."
      ]
    },
    {
      "type": "h2",
      "text": "Intervenções"
    },
    {
      "type": "para",
      "text": "A análise automática começa desligada. Em IA, escolha Sob demanda, Discreto ou Proativo. Os perfis automáticos têm intervalos mínimos de cinco e dois minutos e exigem novas mensagens; perguntas diretas têm prioridade."
    },
    {
      "type": "h2",
      "text": "Atalhos padrão"
    },
    {
      "type": "list",
      "items": [
        "Ctrl + Shift + F8: capturar um comando direto, sem precisar dizer Rocky.",
        "Ctrl + Shift + F9: silenciar ou retomar a voz.",
        "Ctrl + Shift + F10: mostrar ou ocultar a janela.",
        "No Mac, use Control; alguns teclados também exigem Fn. Em Voz, escolha três teclas distintas entre F1 e F12."
      ]
    },
    {
      "type": "h2",
      "text": "Janela"
    },
    {
      "type": "para",
      "text": "Posição e tamanho da janela expandida são persistidos. Use os controles de modo compacto e fixação no topo conforme sua transmissão."
    }
  ]
};

export default content;
