const content = {
  "id": "canal-sete",
  "name": "Canal Sete",
  "category": "Podcast",
  "filter": "Podcast",
  "quote": "Ler superchat em voz alta virou parte do programa.",
  "desc": "Podcast semanal com convidados. O host ouve o Rocky no retorno.",
  "stack": ["OpenAI", "ElevenLabs"],
  "dots": ["#ff0033", "#1877f2"],
  "tint": "rgba(255,0,51,.06)",
  "links": [{"label": "youtube.com/@canalsete", "url": "https://youtube.com/@canalsete", "dot": "#ff0033"}],
  "stats": [{"label": "PLATAFORMAS", "value": "2"}, {"label": "PICO DE CHAT", "value": "1,4 mil"}, {"label": "SUPERCHATS / EP", "value": "~30"}, {"label": "MODELO", "value": "Remoto"}],
  "story": ["Somos um podcast ao vivo de duas horas com convidado. A parte mais difícil da produção sempre foi o superchat: alguém manda cinquenta reais com uma pergunta e ninguém vê, porque o host está conduzindo a conversa e o operador está cuidando de áudio.", "Hoje o Rocky lê tudo no retorno do host, com nome, valor e mensagem. Ele decide quando encaixar. Deixamos o valor mínimo em dez reais para leitura automática — abaixo disso a fila espera uma brecha.", "Usamos ElevenLabs porque a voz entra no retorno junto com o programa e queríamos algo que não soasse robótico ao lado da voz humana. É a única parte da nossa configuração que não é local, e é uma escolha consciente."],
  "changes": ["Nenhum superchat passa batido, inclusive nos blocos mais longos.", "O operador de áudio parou de anotar perguntas em papel.", "A média de contribuições por episódio subiu depois que o público percebeu que sempre é lido."],
  "setup": [{"label": "IA", "value": "openai"}, {"label": "Voz", "value": "elevenlabs"}, {"label": "Tom", "value": "animado"}, {"label": "Superchats", "value": "auto ≥ R$ 10"}, {"label": "Interrupção", "value": "ligada"}],
  "config": "[superchats]\nauto_read = true\nmin_value = 10.0\n\n[agent]\ntone      = \"animado\"\ninterrupt = true",
  "sample": "Superchat de Helena, cinquenta reais: vocês vão comentar o caso da semana passada?",
};

export default content;
