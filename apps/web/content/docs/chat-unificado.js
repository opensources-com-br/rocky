const content = {
  group: "Recursos",
  lead: "Todas as plataformas conectadas viram uma fila só, em ordem de chegada, com a origem de cada mensagem preservada.",
  blocks: [
    {"type": "h2", "text": "Como a fila funciona"},
    {"type": "para", "text": "Cada mensagem entra com sua plataforma, autor e horário. O Rocky agrupa o que é a mesma pergunta feita por pessoas diferentes e ignora repetição do mesmo autor, emote solto e conversa paralela."},
    {"type": "h2", "text": "Filtros aplicados por padrão"},
    {"type": "list", "items": ["Mensagens idênticas do mesmo autor em menos de trinta segundos.", "Mensagens compostas apenas de emotes ou pontuação.", "Comandos de bot e links de moderação.", "Usuários que você silenciou na plataforma de origem."]},
    {"type": "h2", "text": "Pulso"},
    {"type": "para", "text": "A aba Pulso mostra mensagens por minuto e espectadores de cada plataforma. Serve para perceber quando um canal esquentou sem você notar, ou quando a transmissão caiu em um deles."},
    {"type": "note", "text": "A contagem de espectadores no rodapé da janela soma apenas as plataformas com o chip ligado."},
  ],
};

export default content;
