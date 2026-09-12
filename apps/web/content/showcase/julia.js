const content = {
  "id": "julia",
  "name": "Programando ao vivo",
  "category": "Programação",
  "filter": "Programação",
  "quote": "Consultar dúvidas enquanto escreve código",
  "desc": "Cenário ilustrativo. Use perguntas por texto para consultar a amostra recente do chat enquanto programa.",
  "stack": ["Ollama", "Voz do sistema"],
  "dots": ["#9146ff"],
  "tint": "rgba(212,112,60,.06)",
  "links": [],
  "stats": [{"label": "INTEGRAÇÃO", "value": "Twitch"}, {"label": "PERFIL", "value": "Sob demanda"}, {"label": "DADOS", "value": "Exemplo"}, {"label": "IA", "value": "Ollama"}],
  "story": ["Use perguntas por texto para consultar a amostra recente do chat enquanto programa.", "Este cenário não é um depoimento de usuário nem um resultado medido. Configure e teste o provedor no seu computador; consultas de chat usam até 200 mensagens recebidas nos últimos dois minutos.", "Se usar IA remota, o pedido e a amostra de chat vão ao provedor. Para voz, prepare whisper.cpp e uma voz do sistema. Verifique a captura de tela e áudio no OBS antes da transmissão."],
  "changes": ["Consultar perguntas recentes da Twitch.", "Salvar notas ou ideias escolhidas por você.", "Revisar registros e perguntas pendentes da sessão."],
  "setup": [{"label": "Plataforma", "value": "Twitch"}, {"label": "IA", "value": "Ollama"}, {"label": "Modelo", "value": "llama3.2"}, {"label": "Voz", "value": "Sistema + whisper.cpp opcional"}, {"label": "Perfil", "value": "Sob demanda"}, {"label": "Tom", "value": "Direto"}],
  "config": "Preencha nas Configurações do Rocky:\n\nPlataforma: Twitch\nIA: Ollama\nModelo: llama3.2\nVoz: Sistema + whisper.cpp opcional\nPerfil: Sob demanda\nTom: Direto\n\nExemplo descritivo, não importável.",
  "sample": "Quais dúvidas sobre o código apareceram no chat?"
};

export default content;
