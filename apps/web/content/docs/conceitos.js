const content = {
  group: "Começando",
  lead: "Cinco ideias que explicam o comportamento do app. Entender essas palavras deixa o resto da documentação mais curto.",
  blocks: [
    {"type": "h2", "text": "Vocabulário"},
    {"type": "cards", "items": [{"key": "fila unificada", "text": "Todas as mensagens de todas as plataformas em ordem de chegada, com a origem marcada."}, {"key": "intervenção", "text": "Uma fala do Rocky no seu monitor. É contada e limitada pela frequência que você define."}, {"key": "turno", "text": "Um par pergunta-resposta entre você e o Rocky, registrado na aba Conversa."}, {"key": "nota", "text": "Um registro com minuto exato: promessa, dúvida sem resposta, problema técnico."}, {"key": "pulso", "text": "Mensagens por minuto e espectadores de cada plataforma conectada."}]},
    {"type": "h2", "text": "Como ele decide o que é relevante"},
    {"type": "para", "text": "Rocky não avalia mensagens uma a uma. Ele olha para janelas de tempo e procura padrões: a mesma pergunta feita por várias pessoas, um pedido que ficou sem resposta, uma reação coletiva forte, um aviso técnico repetido."},
    {"type": "list", "items": ["Repetição: quantas pessoas diferentes pediram a mesma coisa.", "Silêncio: quanto tempo uma pergunta está na fila sem ser respondida.", "Intensidade: variação súbita no ritmo do chat, para cima ou para baixo.", "Dinheiro: superchats e presentes entram com prioridade própria."]},
    {"type": "h2", "text": "O que fica em disco"},
    {"type": "para", "text": "Notas, ideias, turnos de conversa e resumos são arquivos locais em ~/.rocky. Você pode abrir, versionar ou apagar. O histórico do chat não é salvo por padrão."},
  ],
};

export default content;
