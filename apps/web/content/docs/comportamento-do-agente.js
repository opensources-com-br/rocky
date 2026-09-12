const content = {
  group: "Configuração",
  lead: "Quanto, quando e como o Rocky fala. Essas opções mudam mais a sensação de usar o app do que qualquer outra configuração.",
  blocks: [
    {"type": "h2", "text": "Frequência"},
    {"type": "para", "text": "O limite de intervenções por dez minutos é um teto, não uma meta: se nada relevante acontecer, ele fica calado. Chat pequeno funciona bem com duas; acima de mil pessoas, cinco ainda soa natural."},
    {"type": "h2", "text": "Interrupção"},
    {"type": "para", "text": "Com interrupção desligada, o Rocky espera uma pausa de três segundos na sua fala antes de dizer qualquer coisa. Ligada, ele fala assim que tem algo urgente. Streamers que fazem aula costumam deixar desligada; watch parties preferem ligada."},
    {"type": "h2", "text": "Tom"},
    {"type": "table", "cols": "minmax(0,1fr) minmax(0,2fr)", "head": ["TOM", "COMO SOA"], "rows": [["direto", "Fato e número. “Sete pedidos de preço nos últimos dois minutos.”"], ["animado", "Acompanha a energia da live, frases mais curtas."], ["analítico", "Contextualiza antes de concluir, fala um pouco mais."], ["irônico", "Comentário seco. Funciona em chat que já tem esse humor."]]},
    {"type": "h2", "text": "Prioridades"},
    {"type": "para", "text": "Você pode mudar o peso de cada critério de relevância. Quem monetiza por superchat costuma subir dinheiro ao topo; quem dá aula sobe dúvidas sem resposta."},
    {"type": "code", "file": "~/.rocky/config.toml", "text": "[priorities]\nmoney      = 1.0\nunanswered = 0.9\nrepetition = 0.7\nintensity  = 0.5\ntechnical  = 0.8"},
  ],
};

export default content;
