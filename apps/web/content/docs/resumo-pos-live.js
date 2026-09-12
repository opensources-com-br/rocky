const content = {
  group: "Recursos",
  lead: "Ao encerrar a sessão, o Rocky junta o que aconteceu num único documento: o que ficou pendente, o que o chat mais quis saber e onde estão os melhores momentos.",
  blocks: [
    {"type": "h2", "text": "O que entra no resumo"},
    {"type": "list", "items": ["Pendências que você não fechou durante a live.", "Dúvidas repetidas que seguiram sem resposta, com quantas pessoas perguntaram.", "Momentos marcados como candidatos a clipe, com o minuto exato.", "Ideias de pauta acumuladas na sessão.", "Pulso da live: pico de mensagens, pico de espectadores, quedas técnicas."]},
    {"type": "h2", "text": "Formato"},
    {"type": "para", "text": "Um arquivo Markdown por sessão, pronto para colar na descrição do vídeo, virar roteiro de edição ou alimentar sua newsletter. Você pode editar o modelo do resumo."},
    {"type": "code", "file": "~/.rocky/templates/summary.md", "text": "# {{data}} — {{titulo}}\n\n## Ficou pendente\n{{pendencias}}\n\n## O chat mais perguntou\n{{duvidas}}\n\n## Candidatos a clipe\n{{clipes}}\n\n## Próximas pautas\n{{ideias}}"},
    {"type": "note", "text": "O resumo é gerado localmente pelo mesmo modelo que você configurou. Com provedor local, nada sai da máquina em nenhum momento."},
  ],
};

export default content;
