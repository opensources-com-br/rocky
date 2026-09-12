const content = {
  "id": "mesa-de-bar",
  "name": "Mesa de Bar FC",
  "category": "Esportes",
  "filter": "Esportes",
  "quote": "Em jogo grande o chat explode. Ele me dá o placar do humor da torcida.",
  "desc": "Watch party de futebol. Pulso por plataforma sempre aberto na tela.",
  "stack": ["OpenAI", "ElevenLabs"],
  "dots": ["#ff0033", "#1877f2", "#9146ff"],
  "tint": "rgba(255,0,51,.06)",
  "links": [{"label": "youtube.com/@mesadebarfc", "url": "https://youtube.com/@mesadebarfc", "dot": "#ff0033"}, {"label": "twitch.tv/mesadebarfc", "url": "https://twitch.tv/mesadebarfc", "dot": "#9146ff"}],
  "stats": [{"label": "PLATAFORMAS", "value": "3"}, {"label": "PICO DE CHAT", "value": "6,2 mil"}, {"label": "MSG / MIN", "value": "180"}, {"label": "MODELO", "value": "Remoto"}],
  "story": ["Watch party de futebol é o caso extremo: cento e oitenta mensagens por minuto em três plataformas, e todas dizendo a mesma coisa ao mesmo tempo. Ninguém lê aquilo.", "Aqui o Rocky funciona menos como leitor e mais como termômetro. Deixamos a aba Pulso aberta na tela e ele fala quando o humor vira — quando a torcida de uma plataforma reclama de atraso no sinal, quando todo mundo pede replay, quando uma plataforma específica caiu.", "Interrupção ligada, porque numa transmissão de jogo a informação é perecível. Se ele esperar uma pausa na narração, já não serve."],
  "changes": ["Descobrimos atraso de sinal por plataforma em segundos, não no fim do jogo.", "Pedido coletivo de replay chega antes do lance esfriar.", "Superchats são lidos no intervalo, em bloco, sem atropelar o jogo."],
  "setup": [{"label": "IA", "value": "openai"}, {"label": "Voz", "value": "elevenlabs"}, {"label": "Tom", "value": "animado"}, {"label": "Contexto", "value": "2 min"}, {"label": "Interrupção", "value": "ligada"}],
  "config": "[ai]\nprovider = \"openai\"\ncontext_window = \"2min\"\n\n[agent]\ntone      = \"animado\"\ninterrupt = true\ninterventions_per_10min = 6",
  "sample": "O chat do YouTube está vinte segundos atrás do da Twitch e reclamando.",
};

export default content;
