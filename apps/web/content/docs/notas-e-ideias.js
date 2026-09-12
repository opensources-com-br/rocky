const content = {
  "group": "Recursos",
  "lead": "Notas e ideias são registros locais que você cria, edita, pesquisa e organiza por live. Ideias não são geradas e salvas automaticamente.",
  "blocks": [
    {
      "type": "h2",
      "text": "Salvar"
    },
    {
      "type": "list",
      "items": [
        "Use Salvar como nota na sugestão atual ou salve uma resposta pelo Histórico como nota ou ideia.",
        "Diga “Rocky, salva isso como nota” ou “Rocky, salva isso como ideias” para salvar a resposta atual.",
        "Para ditado livre: “Rocky, anota: preparar uma live sobre Kotlin” ou “Rocky, ideia: entrevistar um convidado”.",
        "O salvamento mantém a aba atual e oferece Desfazer. Também é possível criar e editar registros pela interface."
      ]
    },
    {
      "type": "h2",
      "text": "Organizar"
    },
    {
      "type": "para",
      "text": "Busque texto, filtre registros por live e marque ideias realizadas. Perguntas detectadas pela fila são salvas automaticamente; isso não significa que Rocky registra todas as promessas ou problemas da transmissão."
    },
    {
      "type": "h2",
      "text": "Momentos"
    },
    {
      "type": "para",
      "text": "Diga “Rocky, marca esse momento: demonstração do projeto”. O registro guarda o deslocamento desde o início da sessão conectada. Não detecta clipes automaticamente, não corta vídeo e o tempo pode diferir do VOD se Rocky conectar depois do início da live."
    },
    {
      "type": "h2",
      "text": "Persistência"
    },
    {
      "type": "para",
      "text": "Registros ficam em SQLite local, não em uma árvore automática de arquivos Markdown por sessão. Há exportação Markdown de notas e ideias, além de backup JSON em Dados. O histórico de respostas é temporário; salve o que deseja manter."
    }
  ]
};

export default content;
