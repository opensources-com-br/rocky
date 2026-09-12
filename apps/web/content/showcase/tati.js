const content = {
  "id": "tati",
  "name": "Jogando com o chat",
  "category": "Games",
  "filter": "Games",
  "quote": "Consultar o chat usando um atalho",
  "desc": "Cenário ilustrativo. Configure o atalho global de comando e teste o reconhecimento antes de abrir o jogo.",
  "stack": ["Ollama", "Voz do sistema"],
  "dots": ["#9146ff"],
  "tint": "rgba(212,112,60,.06)",
  "links": [],
  "stats": [{"label": "INTEGRAÇÃO", "value": "Twitch"}, {"label": "PERFIL", "value": "Sob demanda"}, {"label": "DADOS", "value": "Exemplo"}, {"label": "IA", "value": "Ollama"}],
  "story": ["Configure o atalho global de comando e teste o reconhecimento antes de abrir o jogo.", "Este cenário não é um depoimento de usuário nem um resultado medido. Configure e teste o provedor no seu computador; consultas de chat usam até 200 mensagens recebidas nos últimos dois minutos.", "Se usar IA remota, o pedido e a amostra de chat vão ao provedor. Para voz, prepare whisper.cpp e uma voz do sistema. Verifique a captura de tela e áudio no OBS antes da transmissão."],
  "changes": ["Consultar perguntas recentes da Twitch.", "Salvar notas ou ideias escolhidas por você.", "Revisar registros e perguntas pendentes da sessão."],
  "setup": [{"label": "Plataforma", "value": "Twitch"}, {"label": "IA", "value": "Ollama"}, {"label": "Modelo", "value": "llama3.2"}, {"label": "Voz", "value": "Sistema + whisper.cpp opcional"}, {"label": "Perfil", "value": "Sob demanda"}, {"label": "Tom", "value": "Animado"}],
  "config": "Preencha nas Configurações do Rocky:\n\nPlataforma: Twitch\nIA: Ollama\nModelo: llama3.2\nVoz: Sistema + whisper.cpp opcional\nPerfil: Sob demanda\nTom: Animado\n\nExemplo descritivo, não importável.",
  "sample": "O que o chat está perguntando agora?"
};

export default content;
