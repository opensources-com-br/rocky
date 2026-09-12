const content = {
  "id": "marcos",
  "name": "Explicando um deploy",
  "category": "Programação",
  "filter": "Programação",
  "quote": "Retomar uma explicação com contexto",
  "desc": "Cenário ilustrativo. Pergunte e use “explica melhor” para continuar uma resposta da sessão.",
  "stack": ["OpenAI API", "Voz do sistema"],
  "dots": ["#9146ff"],
  "tint": "rgba(212,112,60,.06)",
  "links": [],
  "stats": [{"label": "INTEGRAÇÃO", "value": "Twitch"}, {"label": "PERFIL", "value": "Sob demanda"}, {"label": "DADOS", "value": "Exemplo"}, {"label": "IA", "value": "OpenAI API"}],
  "story": ["Pergunte e use “explica melhor” para continuar uma resposta da sessão.", "Este cenário não é um depoimento de usuário nem um resultado medido. Configure e teste o provedor no seu computador; consultas de chat usam até 200 mensagens recebidas nos últimos dois minutos.", "Se usar IA remota, o pedido e a amostra de chat vão ao provedor. Para voz, prepare whisper.cpp e uma voz do sistema. Verifique a captura de tela e áudio no OBS antes da transmissão."],
  "changes": ["Consultar perguntas recentes da Twitch.", "Salvar notas ou ideias escolhidas por você.", "Revisar registros e perguntas pendentes da sessão."],
  "setup": [{"label": "Plataforma", "value": "Twitch"}, {"label": "IA", "value": "OpenAI API"}, {"label": "Modelo", "value": "modelo disponível na sua conta"}, {"label": "Voz", "value": "Sistema + whisper.cpp opcional"}, {"label": "Perfil", "value": "Sob demanda"}, {"label": "Tom", "value": "Analítico"}],
  "config": "Preencha nas Configurações do Rocky:\n\nPlataforma: Twitch\nIA: OpenAI API\nModelo: modelo disponível na sua conta\nVoz: Sistema + whisper.cpp opcional\nPerfil: Sob demanda\nTom: Analítico\n\nExemplo descritivo, não importável.",
  "sample": "Resume as dúvidas recentes sobre o deploy."
};

export default content;
