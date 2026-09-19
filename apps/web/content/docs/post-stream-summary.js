const content = {
  "group": "Recursos",
  "lead": "Ao desconectar a sessão ou fechar o app, Rocky cria um resumo local dos registros associados àquela live.",
  "blocks": [
    {
      "type": "h2",
      "text": "Conteúdo"
    },
    {
      "type": "para",
      "text": "O resumo reúne os registros salvos da sessão, incluindo notas, ideias, momentos e perguntas pendentes. Só pode resumir o que foi registrado; não analisa a gravação nem recupera todo o chat."
    },
    {
      "type": "h2",
      "text": "Como é produzido"
    },
    {
      "type": "para",
      "text": "O resumo é montado pelo código a partir dos registros, sem chamar o modelo de IA. Ele é salvo como um registro local no SQLite. Não há templates editáveis de resumo nem arquivo summary.md gerado automaticamente."
    },
    {
      "type": "h2",
      "text": "Limites"
    },
    {
      "type": "para",
      "text": "Não inclui uma análise automática de picos, quedas técnicas ou melhores clipes. Os momentos são os marcados por você, relativos à conexão do Rocky. Mantenha o estado das perguntas e ideias atualizado para o resumo refletir suas pendências."
    }
  ]
};

export default content;
