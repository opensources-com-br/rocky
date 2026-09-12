const content = {
  "id": "mesa-de-bar",
  "name": "Comentando esportes",
  "category": "Esportes",
  "filter": "Esportes",
  "quote": "Marcar momentos para revisar depois",
  "desc": "Cenário ilustrativo. Marque um momento por voz. O registro usa o tempo da sessão conectada, sem gravar ou cortar a transmissão.",
  "stack": ["OpenRouter", "Voz do sistema"],
  "dots": ["#9146ff"],
  "tint": "rgba(212,112,60,.06)",
  "links": [],
  "stats": [{"label": "INTEGRAÇÃO", "value": "Twitch"}, {"label": "PERFIL", "value": "Proativo"}, {"label": "DADOS", "value": "Exemplo"}, {"label": "IA", "value": "OpenRouter"}],
  "story": ["Marque um momento por voz. O registro usa o tempo da sessão conectada, sem gravar ou cortar a transmissão.", "Este cenário não é um depoimento de usuário nem um resultado medido. Configure e teste o provedor no seu computador; consultas de chat usam até 200 mensagens recebidas nos últimos dois minutos.", "Se usar IA remota, o pedido e a amostra de chat vão ao provedor. Para voz, prepare whisper.cpp e uma voz do sistema. Verifique a captura de tela e áudio no OBS antes da transmissão."],
  "changes": ["Consultar perguntas recentes da Twitch.", "Salvar notas ou ideias escolhidas por você.", "Revisar registros e perguntas pendentes da sessão."],
  "setup": [{"label": "Plataforma", "value": "Twitch"}, {"label": "IA", "value": "OpenRouter"}, {"label": "Modelo", "value": "openrouter/free"}, {"label": "Voz", "value": "Sistema + whisper.cpp opcional"}, {"label": "Perfil", "value": "Proativo"}, {"label": "Tom", "value": "Animado"}],
  "config": "Preencha nas Configurações do Rocky:\n\nPlataforma: Twitch\nIA: OpenRouter\nModelo: openrouter/free\nVoz: Sistema + whisper.cpp opcional\nPerfil: Proativo\nTom: Animado\n\nExemplo descritivo, não importável.",
  "sample": "Rocky, marca esse momento: debate com o chat."
};

export default content;
