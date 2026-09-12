const content = {
  group: "Recursos",
  lead: "Enquanto você transmite, o Rocky registra o que precisa de volta depois: promessas, dúvidas sem resposta, problemas técnicos e pautas que o chat pediu.",
  blocks: [
    {"type": "h2", "text": "Notas"},
    {"type": "para", "text": "Uma nota tem minuto exato, texto e etiqueta. Elas nascem de três lugares: do próprio Rocky quando ele percebe algo, de você pressionando ⌥ N durante uma fala, ou de um pedido por voz."},
    {"type": "table", "cols": "minmax(0,1fr) minmax(0,2fr)", "head": ["ETIQUETA", "QUANDO APARECE"], "rows": [["pendência", "Você prometeu algo e ainda não voltou ao assunto"], ["dúvida", "Pergunta repetida que segue sem resposta"], ["clipe", "Reação coletiva forte — candidato a corte"], ["técnico", "Queda de áudio, travamento, aviso do chat"]]},
    {"type": "h2", "text": "Ideias"},
    {"type": "para", "text": "Ideias são pautas para depois, não coisas para resolver agora: uma série a partir das dúvidas repetidas, uma enquete que o chat pediu, um convidado que apareceu respondendo todo mundo."},
    {"type": "h2", "text": "Onde ficam"},
    {"type": "code", "file": "Terminal", "text": "~/.rocky/sessions/2026-09-11-2137/\n  notes.md\n  ideas.md\n  conversation.md\n  summary.md"},
    {"type": "para", "text": "Arquivos Markdown simples, um diretório por sessão. Você pode versionar, sincronizar ou apagar como qualquer outro arquivo seu."},
  ],
};

export default content;
