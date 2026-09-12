const content = {
  "id": "atelie",
  "name": "Desenhando ao vivo",
  "category": "Arte",
  "filter": "Arte",
  "quote": "Guardar sugestões de novas pautas",
  "desc": "Cenário ilustrativo. Salve uma ideia por ditado livre e marque como realizada quando produzir o conteúdo.",
  "stack": ["Ollama", "Voz do sistema"],
  "dots": ["#9146ff"],
  "tint": "rgba(212,112,60,.06)",
  "links": [],
  "stats": [{"label": "INTEGRAÇÃO", "value": "Twitch"}, {"label": "PERFIL", "value": "Sob demanda"}, {"label": "DADOS", "value": "Exemplo"}, {"label": "IA", "value": "Ollama"}],
  "story": ["Salve uma ideia por ditado livre e marque como realizada quando produzir o conteúdo.", "Este cenário não é um depoimento de usuário nem um resultado medido. Configure e teste o provedor no seu computador; consultas de chat usam até 200 mensagens recebidas nos últimos dois minutos.", "Se usar IA remota, o pedido e a amostra de chat vão ao provedor. Para voz, prepare whisper.cpp e uma voz do sistema. Verifique a captura de tela e áudio no OBS antes da transmissão."],
  "changes": ["Consultar perguntas recentes da Twitch.", "Salvar notas ou ideias escolhidas por você.", "Revisar registros e perguntas pendentes da sessão."],
  "setup": [{"label": "Plataforma", "value": "Twitch"}, {"label": "IA", "value": "Ollama"}, {"label": "Modelo", "value": "llama3.2"}, {"label": "Voz", "value": "Sistema + whisper.cpp opcional"}, {"label": "Perfil", "value": "Sob demanda"}, {"label": "Tom", "value": "Direto"}],
  "config": "Preencha nas Configurações do Rocky:\n\nPlataforma: Twitch\nIA: Ollama\nModelo: llama3.2\nVoz: Sistema + whisper.cpp opcional\nPerfil: Sob demanda\nTom: Direto\n\nExemplo descritivo, não importável.",
  "sample": "Rocky, ideia: desenhar uma cidade na chuva."
};

export default content;
