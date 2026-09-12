const content = {
  "id": "tati",
  "name": "tati.plays",
  "category": "Games",
  "filter": "Games",
  "quote": "Em raid grande o chat vira ruído. O Rocky filtra e me diz o que interessa.",
  "desc": "FPS competitivo. Intervenções limitadas a duas por dez minutos.",
  "stack": ["Ollama · qwen2.5", "Piper"],
  "dots": ["#9146ff", "#53fc18"],
  "tint": "rgba(83,252,24,.06)",
  "links": [{"label": "twitch.tv/tatiplays", "url": "https://twitch.tv/tatiplays", "dot": "#9146ff"}, {"label": "kick.com/tatiplays", "url": "https://kick.com/tatiplays", "dot": "#53fc18"}],
  "stats": [{"label": "PLATAFORMAS", "value": "2"}, {"label": "PICO DE CHAT", "value": "3,8 mil"}, {"label": "MSG / MIN", "value": "90+"}, {"label": "MODELO", "value": "Local 7B"}],
  "story": ["Jogo FPS competitivo, então noventa por cento da minha atenção está na partida. Quando entra uma raid de três mil pessoas, o chat deixa de ser legível para qualquer ser humano.", "Configurei o Rocky no extremo oposto do que a maioria usa: duas intervenções por dez minutos, tom direto, interrupção desligada. Ele fala pouquíssimo. Mas quando fala, é algo que eu precisava saber — uma raid chegando, um problema de áudio, uma pergunta que metade do chat está fazendo.", "Rodo um qwen2.5 local porque não quero nenhuma dependência de internet além da própria transmissão. Se a conexão oscila, a live cai — mas o assistente não."],
  "changes": ["Consigo agradecer raid pelo nome sem sair da partida.", "Aviso de problema técnico chega por voz em vez de eu descobrir no fim da live.", "Chat de noventa mensagens por minuto virou duas frases úteis."],
  "setup": [{"label": "IA", "value": "ollama / qwen2.5:7b"}, {"label": "Voz", "value": "piper · pt_BR-edresson"}, {"label": "Tom", "value": "direto"}, {"label": "Frequência", "value": "2 / 10min"}, {"label": "Prioridade", "value": "técnico alto"}],
  "config": "[agent]\ntone      = \"direto\"\ninterrupt = false\ninterventions_per_10min = 2\n\n[priorities]\ntechnical = 1.0\nintensity = 0.9",
  "sample": "Raid de mil e duzentas pessoas entrando agora, canal da Nina.",
};

export default content;
