const content = {
  "id": "julia",
  "name": "ju.lia",
  "category": "Programação",
  "filter": "Programação",
  "quote": "Parei de perder pergunta boa no meio de 40 mensagens por minuto.",
  "desc": "Lives de 4h sobre backend. Modelo local de 8B, quatro plataformas somadas.",
  "stack": ["Ollama · llama3.1:8b", "Piper"],
  "dots": ["#9146ff", "#ff0033", "#53fc18", "#1877f2"],
  "tint": "rgba(212,112,60,.06)",
  "links": [{"label": "twitch.tv/julia", "url": "https://twitch.tv/julia", "dot": "#9146ff"}, {"label": "youtube.com/@julia", "url": "https://youtube.com/@julia", "dot": "#ff0033"}, {"label": "kick.com/julia", "url": "https://kick.com/julia", "dot": "#53fc18"}],
  "stats": [{"label": "PLATAFORMAS", "value": "4"}, {"label": "PICO DE CHAT", "value": "2,1 mil"}, {"label": "MSG / MIN", "value": "42"}, {"label": "MODELO", "value": "Local 8B"}],
  "story": ["Transmito quatro horas seguidas, três vezes por semana, sobre backend. O chat soma Twitch, YouTube, Kick e Facebook, e nos picos passa de duas mil pessoas. Chegou num ponto em que eu tinha duas opções: parar de codar para ler o chat, ou ignorar o chat e codar. Nenhuma das duas é uma live boa.", "Instalei o Rocky num sábado à tarde para testar e não desliguei mais. A primeira coisa que ele fez foi me avisar que sete pessoas tinham perguntado o preço do curso em dois minutos. Eu não tinha visto uma única delas.", "Rodo em Ollama com um llama3.1 de 8B na mesma máquina da transmissão. Achei que ia competir com o encoder, mas a queda de quadros ficou igual: 0,2%. O custo é zero e nenhuma mensagem do meu chat sai do meu computador, o que para mim pesa mais que a qualidade de um modelo maior.", "O que me surpreendeu não foi a leitura do chat, foi o resumo do fim. Eu encerrava a live sem saber o que tinha prometido. Agora abro o arquivo da sessão e está tudo lá, com o minuto exato."],
  "changes": ["Dúvida repetida sem resposta caiu de dezenas por live para praticamente nenhuma.", "O resumo pós-live virou o roteiro de edição do corte que sobe no dia seguinte.", "Paro de pedir para o chat repetir pergunta — o Rocky já agrupou."],
  "setup": [{"label": "IA", "value": "ollama / llama3.1:8b"}, {"label": "Voz", "value": "piper · pt_BR-faber"}, {"label": "Tom", "value": "direto"}, {"label": "Frequência", "value": "3 / 10min"}, {"label": "Interrupção", "value": "desligada"}],
  "config": "[ai]\nprovider = \"ollama\"\nmodel    = \"llama3.1:8b\"\n\n[agent]\ntone      = \"direto\"\ninterrupt = false\ninterventions_per_10min = 3",
  "sample": "Três pessoas pediram o link do repositório desde que você abriu o editor.",
};

export default content;
