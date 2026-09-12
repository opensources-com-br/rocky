const content = {
  "group": "Começando",
  "lead": "Rocky é um assistente desktop para macOS e Windows. A versão atual acompanha novas mensagens do seu canal na Twitch, Kick ou YouTube e ajuda a consultar o chat, organizar perguntas e salvar registros.",
  "blocks": [
    {
      "type": "h2",
      "text": "O que funciona hoje"
    },
    {
      "type": "list",
      "items": [
        "Perguntas por texto e, com reconhecimento configurado, por voz.",
        "Sugestões com Ollama, OpenAI API ou OpenRouter, usando uma amostra recente do chat.",
        "Fila de perguntas agrupadas, histórico da sessão, notas, ideias, momentos e resumo dos registros.",
        "Janela redimensionável, modo compacto e opção de manter no topo."
      ]
    },
    {
      "type": "h2",
      "text": "Limites da versão atual"
    },
    {
      "type": "para",
      "text": "É uma versão alpha com instaladores de desenvolvimento não assinados. Facebook, Super Chats, envio de mensagens e plugins não estão integrados. A Kick exige um webhook HTTPS público. O app não modera o chat nem grava a transmissão."
    },
    {
      "type": "h2",
      "text": "Áudio e privacidade"
    },
    {
      "type": "para",
      "text": "O app não exige conta Rocky nem inclui backend próprio. Twitch, Kick e YouTube precisam de internet; a Kick também precisa de encaminhamento HTTPS. Provedores de IA remotos recebem o pedido e a amostra de chat. Confira as fontes do OBS com uma gravação de teste."
    },
    {
      "type": "h2",
      "text": "Código aberto"
    },
    {
      "type": "para",
      "text": "O projeto é distribuído sob licença MIT. A interface oferece inglês e português do Brasil."
    }
  ]
};

export default content;
