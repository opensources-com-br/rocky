const content = {
  "id": "canal-sete",
  "name": "Conduzindo um podcast",
  "category": "Podcast",
  "filter": "Podcast",
  "quote": "Separar perguntas para os convidados",
  "desc": "Cenário ilustrativo. Abra a fila de perguntas da Twitch e marque as que já foram respondidas. Apoios pagos não são integrados.",
  "stack": ["OpenRouter", "Voz do sistema"],
  "dots": ["#9146ff"],
  "tint": "rgba(212,112,60,.06)",
  "links": [],
  "stats": [{"label": "INTEGRAÇÃO", "value": "Twitch"}, {"label": "PERFIL", "value": "Discreto"}, {"label": "DADOS", "value": "Exemplo"}, {"label": "IA", "value": "OpenRouter"}],
  "story": ["Abra a fila de perguntas da Twitch e marque as que já foram respondidas. Apoios pagos não são integrados.", "Este cenário não é um depoimento de usuário nem um resultado medido. Configure e teste o provedor no seu computador; consultas de chat usam até 200 mensagens recebidas nos últimos dois minutos.", "Se usar IA remota, o pedido e a amostra de chat vão ao provedor. Para voz, prepare whisper.cpp e uma voz do sistema. Verifique a captura de tela e áudio no OBS antes da transmissão."],
  "changes": ["Consultar perguntas recentes da Twitch.", "Salvar notas ou ideias escolhidas por você.", "Revisar registros e perguntas pendentes da sessão."],
  "setup": [{"label": "Plataforma", "value": "Twitch"}, {"label": "IA", "value": "OpenRouter"}, {"label": "Modelo", "value": "openrouter/free"}, {"label": "Voz", "value": "Sistema + whisper.cpp opcional"}, {"label": "Perfil", "value": "Discreto"}, {"label": "Tom", "value": "Direto"}],
  "config": "Preencha nas Configurações do Rocky:\n\nPlataforma: Twitch\nIA: OpenRouter\nModelo: openrouter/free\nVoz: Sistema + whisper.cpp opcional\nPerfil: Discreto\nTom: Direto\n\nExemplo descritivo, não importável.",
  "sample": "Quais perguntas recentes podemos levar ao convidado?"
};

export default content;
