const content = {
  "group": "Configuração",
  "lead": "Twitch e Kick recebem novas mensagens e a contagem atual de espectadores do canal autenticado.",
  "blocks": [
    {
      "type": "h2",
      "text": "Conectar Twitch"
    },
    {
      "type": "h2",
      "text": "Conectar Kick"
    },
    {
      "type": "list",
      "items": [
        "Crie um aplicativo em dev.kick.com e cadastre o callback http://localhost:18181/oauth/kick/callback.",
        "Cadastre também uma URL pública HTTPS que encaminhe corpo e cabeçalhos do webhook para http://localhost:18181/webhooks/kick.",
        "Informe Client ID e Client Secret, use Conectar Kick, abra a autorização e mantenha o encaminhamento ativo durante a live."
      ]
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
      "text": "A Twitch usa Device Code Flow e EventSub WebSocket. A Kick usa OAuth 2.1, permissões user:read, channel:read e events:subscribe, e webhooks assinados. O Client Secret da Kick usa o cofre do sistema; tokens ficam somente na memória."
    },
    {
      "type": "h2",
      "text": "O que não está integrado"
    },
    {
      "type": "para",
      "text": "YouTube e Facebook aparecem como indisponíveis. Os chips são indicadores, não interruptores. Rocky não envia mensagens ao chat e não recebe subs, bits, raids, pontos do canal, presentes ou Super Chats como eventos de apoio."
    },
    {
      "type": "h2",
      "text": "Reconexão"
    },
    {
      "type": "para",
      "text": "A Twitch tenta reconectar automaticamente. Na Kick, mantenha o webhook HTTPS encaminhando ao app aberto. Nenhum conector carrega histórico; mensagens podem ser perdidas durante indisponibilidade."
    }
  ]
};

export default content;
