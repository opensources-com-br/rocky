const content = {
  "id": "bruno",
  "name": "Bruno Silva",
  "category": "Educação",
  "filter": "Educação",
  "quote": "O resumo pós-live me economiza uma hora de edição por transmissão.",
  "desc": "Curso de dados ao vivo, três vezes por semana.",
  "stack": ["Anthropic", "Voz do macOS"],
  "dots": ["#ff0033"],
  "tint": "rgba(24,119,242,.06)",
  "links": [{"label": "youtube.com/@brunosilva", "url": "https://youtube.com/@brunosilva", "dot": "#ff0033"}],
  "stats": [{"label": "PLATAFORMAS", "value": "1"}, {"label": "PICO DE CHAT", "value": "890"}, {"label": "AULAS / SEMANA", "value": "3"}, {"label": "MODELO", "value": "Remoto"}],
  "story": ["Dou aula de análise de dados três vezes por semana, cada uma com quase novecentas pessoas. A aula em si eu domino; o gargalo sempre foi o depois.", "Cada transmissão gerava uma hora de trabalho: rever a gravação para achar o que ficou pendente, listar dúvidas para responder no fórum, marcar os trechos que valem corte.", "O resumo do Rocky entrega isso pronto. Reviso, ajusto duas frases e publico. É a diferença entre três horas de pós-produção por semana e vinte minutos."],
  "changes": ["Pós-produção caiu de uma hora por aula para cerca de vinte minutos por semana.", "As dúvidas sem resposta vão para o fórum no mesmo dia.", "Os trechos marcados como clipe dispensam rever a gravação inteira."],
  "setup": [{"label": "IA", "value": "anthropic"}, {"label": "Voz", "value": "system · Luciana"}, {"label": "Tom", "value": "analítico"}, {"label": "Contexto", "value": "live inteira"}, {"label": "Resumo", "value": "ligado"}],
  "config": "[ai]\nprovider = \"anthropic\"\ncontext_window = \"full\"\n\n[agent]\ntone = \"analitico\"\n\n[summary]\nenabled = true",
  "sample": "Onze pessoas pediram o notebook desta aula. Nenhuma foi respondida ainda.",
};

export default content;
