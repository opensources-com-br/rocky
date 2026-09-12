const content = {
  "id": "renan",
  "name": "renan__",
  "category": "Games",
  "filter": "Games",
  "quote": "Rodo tudo offline. Nenhuma mensagem do meu chat sai da minha máquina.",
  "desc": "Speedruns noturnos. Modelo local em um Mac mini dedicado.",
  "stack": ["LM Studio", "Piper"],
  "dots": ["#9146ff"],
  "tint": "rgba(145,70,255,.06)",
  "links": [{"label": "twitch.tv/renan__", "url": "https://twitch.tv/renan__", "dot": "#9146ff"}],
  "stats": [{"label": "PLATAFORMAS", "value": "1"}, {"label": "PICO DE CHAT", "value": "310"}, {"label": "MSG / MIN", "value": "22"}, {"label": "MODELO", "value": "Local 14B"}],
  "story": ["Escolhi o Rocky por um motivo só: privacidade. Meu chat é pequeno e fiel, e várias conversas ali são pessoais. Não quero que passem por um serviço de terceiros para virar sugestão de pauta.", "Rodo um modelo de 14B no LM Studio, num Mac mini separado que só faz isso. A máquina de transmissão fala com ele pela rede local. Fica melhor que qualquer coisa que eu rodaria junto do jogo, e continua tudo dentro de casa.", "Como o modelo é maior, a redação das intervenções é visivelmente melhor. Ele consegue resumir três mensagens diferentes numa frase que faz sentido, em vez de só contar quantas vezes algo apareceu."],
  "changes": ["Zero dependência de serviço externo durante a live.", "Intervenções mais bem escritas por causa do modelo maior.", "Máquina de transmissão sem carga extra de inferência."],
  "setup": [{"label": "IA", "value": "openai-compat (LM Studio)"}, {"label": "Base URL", "value": "http://192.168.0.12:1234"}, {"label": "Voz", "value": "piper · pt_BR-faber"}, {"label": "Tom", "value": "irônico"}, {"label": "Frequência", "value": "3 / 10min"}],
  "config": "[ai]\nprovider = \"openai-compat\"\nbase_url = \"http://192.168.0.12:1234\"\nmodel    = \"qwen2.5-14b\"\n\n[agent]\ntone = \"ironico\"",
  "sample": "O chat inteiro percebeu que você errou o mesmo pulo pela quarta vez.",
};

export default content;
