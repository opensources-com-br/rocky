const content = {
  "id": "bruno",
  "name": "Respondendo dúvidas de uma aula",
  "category": "Educação",
  "filter": "Educação",
  "quote": "Revisar pendências registradas",
  "desc": "Cenário ilustrativo. Ao encerrar a sessão, consulte o resumo dos registros e das perguntas que continuam pendentes.",
  "stack": ["OpenAI API", "Voz do sistema"],
  "dots": ["#9146ff"],
  "tint": "rgba(212,112,60,.06)",
  "links": [],
  "stats": [{"label": "INTEGRAÇÃO", "value": "Twitch"}, {"label": "PERFIL", "value": "Discreto"}, {"label": "DADOS", "value": "Exemplo"}, {"label": "IA", "value": "OpenAI API"}],
  "story": ["Ao encerrar a sessão, consulte o resumo dos registros e das perguntas que continuam pendentes.", "Este cenário não é um depoimento de usuário nem um resultado medido. Configure e teste o provedor no seu computador; consultas de chat usam até 200 mensagens recebidas nos últimos dois minutos.", "Se usar IA remota, o pedido e a amostra de chat vão ao provedor. Para voz, prepare whisper.cpp e uma voz do sistema. Verifique a captura de tela e áudio no OBS antes da transmissão."],
  "changes": ["Consultar perguntas recentes da Twitch.", "Salvar notas ou ideias escolhidas por você.", "Revisar registros e perguntas pendentes da sessão."],
  "setup": [{"label": "Plataforma", "value": "Twitch"}, {"label": "IA", "value": "OpenAI API"}, {"label": "Modelo", "value": "modelo disponível na sua conta"}, {"label": "Voz", "value": "Sistema + whisper.cpp opcional"}, {"label": "Perfil", "value": "Discreto"}, {"label": "Tom", "value": "Analítico"}],
  "config": "Preencha nas Configurações do Rocky:\n\nPlataforma: Twitch\nIA: OpenAI API\nModelo: modelo disponível na sua conta\nVoz: Sistema + whisper.cpp opcional\nPerfil: Discreto\nTom: Analítico\n\nExemplo descritivo, não importável.",
  "sample": "Quais dúvidas principais aparecem na amostra recente?"
};

export default content;
