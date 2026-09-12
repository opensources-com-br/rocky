const content = {
  "group": "Configuração",
  "lead": "A única integração ativa é a Twitch: novas mensagens e contagem atual de espectadores do canal da conta autenticada.",
  "blocks": [
    {
      "type": "h2",
      "text": "Conectar Twitch"
    },
    {
      "type": "list",
      "items": [
        "Abra Configurações → Plataformas e use Conectar Twitch.",
        "Se não houver Client ID configurado, registre um aplicativo público no console de desenvolvedores da Twitch e copie seu Client ID. Rocky não usa Client Secret.",
        "Abra Twitch pelo botão do app e autorize o código exibido. Aguarde o estado Conectada."
      ]
    },
    {
      "type": "h2",
      "text": "Dados e permissões"
    },
    {
      "type": "para",
      "text": "O conector usa Device Code Flow, EventSub WebSocket e a permissão user:read:chat. O Client ID fica nas preferências locais; tokens de acesso e renovação ficam somente na memória e são descartados ao desconectar ou fechar."
    },
    {
      "type": "h2",
      "text": "O que não está integrado"
    },
    {
      "type": "para",
      "text": "Kick, YouTube e Facebook aparecem como indisponíveis. Os chips são indicadores, não interruptores. Rocky não envia mensagens ao chat e não recebe subs, bits, raids, pontos do canal, presentes ou Super Chats como eventos de apoio."
    },
    {
      "type": "h2",
      "text": "Reconexão"
    },
    {
      "type": "para",
      "text": "O app tenta reconectar automaticamente após interrupções. Não carrega mensagens históricas e pode perder mensagens durante uma falha de conexão; a conversa sinaliza lacunas na captura."
    }
  ]
};

export default content;
