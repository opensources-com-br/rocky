const content = {
  "id": "atelie",
  "name": "Ateliê da Nô",
  "category": "Arte",
  "filter": "Arte",
  "quote": "Desenho de costas para o chat e ainda assim respondo todo mundo.",
  "desc": "Ilustração ao vivo. Conversa só por voz, mãos sempre ocupadas.",
  "stack": ["Ollama · gemma2", "Piper"],
  "dots": ["#9146ff", "#ff0033"],
  "tint": "rgba(212,112,60,.06)",
  "links": [{"label": "twitch.tv/ateliedano", "url": "https://twitch.tv/ateliedano", "dot": "#9146ff"}, {"label": "youtube.com/@ateliedano", "url": "https://youtube.com/@ateliedano", "dot": "#ff0033"}],
  "stats": [{"label": "PLATAFORMAS", "value": "2"}, {"label": "PICO DE CHAT", "value": "520"}, {"label": "MSG / MIN", "value": "26"}, {"label": "MODELO", "value": "Local 9B"}],
  "story": ["Ilustro em mesa digitalizadora com a tela virada para o trabalho, não para o chat. Antes eu parava a cada dez minutos para ler, o que quebrava o ritmo do desenho e o ritmo da live.", "Hoje só converso. Pergunto “o que estão achando das cores?” e ele me responde. Se alguém quer saber qual pincel estou usando, ele me conta e eu explico em voz alta. Nunca mais toquei no teclado durante uma sessão de desenho.", "Uso um gemma2 local. Não preciso de nada muito sofisticado: meu chat é conversado, não técnico. O que importa é a latência ser baixa o suficiente para parecer diálogo."],
  "changes": ["O desenho não para mais para eu ler chat.", "As ideias de pauta saem das próprias perguntas do público.", "Sessões ficaram mais longas porque o ritmo não quebra."],
  "setup": [{"label": "IA", "value": "ollama / gemma2:9b"}, {"label": "Voz", "value": "piper · pt_BR-faber"}, {"label": "Tom", "value": "animado"}, {"label": "Frequência", "value": "4 / 10min"}, {"label": "Interrupção", "value": "desligada"}],
  "config": "[ai]\nprovider = \"ollama\"\nmodel    = \"gemma2:9b\"\n\n[agent]\ntone = \"animado\"\ninterventions_per_10min = 4",
  "sample": "Duas pessoas querem saber qual pincel você usou nessa textura.",
};

export default content;
