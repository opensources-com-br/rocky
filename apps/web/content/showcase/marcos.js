const content = {
  "id": "marcos",
  "name": "marcos_dev",
  "category": "Programação",
  "filter": "Programação",
  "quote": "Ele me avisa quando prometi algo e não voltei no assunto.",
  "desc": "Pair programming ao vivo. Usa as notas como roteiro do vídeo editado.",
  "stack": ["Anthropic", "Voz do macOS"],
  "dots": ["#9146ff", "#ff0033"],
  "tint": "rgba(145,70,255,.06)",
  "links": [{"label": "twitch.tv/marcos_dev", "url": "https://twitch.tv/marcos_dev", "dot": "#9146ff"}, {"label": "youtube.com/@marcosdev", "url": "https://youtube.com/@marcosdev", "dot": "#ff0033"}],
  "stats": [{"label": "PLATAFORMAS", "value": "2"}, {"label": "PICO DE CHAT", "value": "640"}, {"label": "MSG / MIN", "value": "18"}, {"label": "MODELO", "value": "Remoto"}],
  "story": ["Faço pair programming ao vivo com convidados. O problema não é volume de chat, é atenção: quando estou discutindo arquitetura com outra pessoa, deixo de ver a tela do chat por vinte minutos seguidos.", "O Rocky resolve isso sendo chato na medida. Ele não me interrompe no meio de um raciocínio — deixei a interrupção desligada, ele espera a pausa. Aí fala uma coisa só, a mais importante.", "O uso que virou hábito é outro: as notas. Toda vez que prometo mostrar algo depois, ele registra com o minuto. No fim da live tenho a lista do que ficou pendente e o timestamp de cada momento que vale virar corte."],
  "changes": ["As notas com timestamp substituíram o caderno que eu nunca preenchia.", "O convidado também ouve o Rocky no retorno, então os dois sabem o que o chat quer.", "Promessa esquecida deixou de acontecer."],
  "setup": [{"label": "IA", "value": "anthropic"}, {"label": "Voz", "value": "system · Luciana"}, {"label": "Tom", "value": "analítico"}, {"label": "Frequência", "value": "2 / 10min"}, {"label": "Interrupção", "value": "desligada"}],
  "config": "[ai]\nprovider = \"anthropic\"\n\n[agent]\ntone      = \"analitico\"\ninterrupt = false\ninterventions_per_10min = 2",
  "sample": "Você prometeu mostrar o raio-x do repositório há vinte minutos.",
};

export default content;
