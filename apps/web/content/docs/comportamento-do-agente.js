const content = {
  "group": "Configuração",
  "lead": "O nome, o idioma e o tom ficam em Agente. Os perfis e filtros de análise ficam em IA.",
  "blocks": [
    {
      "type": "h2",
      "text": "Perfis"
    },
    {
      "type": "list",
      "items": [
        "Sob demanda: análise automática desligada; use perguntas diretas ou Analisar agora.",
        "Discreto: intervalo mínimo de cinco minutos para análise automática.",
        "Proativo: intervalo mínimo de dois minutos para análise automática.",
        "Análises automáticas exigem novas mensagens. Perguntas diretas têm prioridade sobre trabalho automático."
      ]
    },
    {
      "type": "h2",
      "text": "Tom e nome"
    },
    {
      "type": "para",
      "text": "Os tons disponíveis são Direto, Animado, Analítico e Irônico. Eles orientam a redação da IA. O nome do agente também é usado como palavra de ativação da voz; o idioma da interface orienta resposta e transcrição."
    },
    {
      "type": "h2",
      "text": "Limites"
    },
    {
      "type": "para",
      "text": "Não há pesos configuráveis para dinheiro, relevância ou urgência. O app não implementa uma pausa garantida de três segundos antes de falar nem um modo de interrupção inteligente do streamer."
    },
    {
      "type": "h2",
      "text": "Histórico"
    },
    {
      "type": "para",
      "text": "Histórico guarda até 30 respostas da sessão, com copiar, repetir, salvar nota e salvar ideia. Os últimos quatro pares ajudam a entender pedidos como “resume isso”. Desconectar limpa o histórico; os registros salvos permanecem."
    }
  ]
};

export default content;
