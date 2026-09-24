const content = {
  "group": "Configuração",
  "lead": "Twitch, Kick, YouTube, Facebook e TikTok LIVE recebem novas mensagens e a contagem atual de espectadores do canal conectado.",
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
      "type": "h2",
      "text": "Conectar YouTube"
    },
    {
      "type": "list",
      "items": [
        "Ative a YouTube Data API v3 e crie credenciais OAuth do tipo aplicativo para computador.",
        "Informe Client ID e Client Secret, conecte e autorize a conta no navegador.",
        "Mantenha uma live ativa com chat habilitado para o Rocky localizar a transmissão."
      ]
    },
    {
      "type": "h2",
      "text": "Conectar Facebook"
    },
    {
      "type": "list",
      "items": [
        "Crie um app Business no Meta for Developers, adicione o produto Login do Facebook e cadastre o callback http://127.0.0.1:18183/oauth/facebook/callback.",
        "Solicite pages_show_list, pages_read_engagement e pages_read_user_content. Informe App ID e App Secret no Rocky e autorize a conta que administra a Página.",
        "Inicie uma live na Página antes de conectar. O Rocky seleciona a primeira Página autorizada com transmissão ativa e acompanha os novos comentários."
      ]
    },
    {
      "type": "h2",
      "text": "Conectar TikTok LIVE"
    },
    {
      "type": "list",
      "items": [
        "Inicie uma live pública no TikTok e copie o nome de usuário do canal, com ou sem @.",
        "No Rocky, abra Configurações → Plataformas → TikTok LIVE, informe o nome e use Conectar TikTok.",
        "A conexão não pede senha nem cookies. Ela usa o protocolo WebCast não oficial por meio da biblioteca TikTokLiveJava e do serviço Eulerstream; alterações do TikTok podem interromper o recurso."
      ]
    },
    {
      "type": "h2",
      "text": "Conexões simultâneas"
    },
    {
      "type": "para",
      "text": "Você pode conectar mais de uma plataforma e desconectar cada uma separadamente. A aba Conversa combina as mensagens recebidas; notas, perguntas e resumo ficam na mesma sessão Rocky até a última plataforma ser desconectada."
    },
    {
      "type": "h2",
      "text": "Dados e permissões"
    },
    {
      "type": "para",
      "text": "A Twitch usa Device Code Flow e EventSub WebSocket. A Kick usa OAuth 2.1 e webhooks assinados. YouTube e Facebook usam OAuth e consultam periodicamente o chat. O TikTok usa o nome público do canal e WebCast não oficial via Eulerstream. Client Secrets e App Secrets usam o cofre do sistema; tokens ficam somente na memória."
    },
    {
      "type": "h2",
      "text": "O que não está integrado"
    },
    {
      "type": "para",
      "text": "Os chips mostram o estado e abrem a ação de desconectar. Rocky não envia mensagens ao chat e não recebe subs, bits, raids, pontos do canal, presentes, Estrelas ou Super Chats como eventos de apoio."
    },
    {
      "type": "h2",
      "text": "Reconexão"
    },
    {
      "type": "para",
      "text": "Twitch e TikTok tentam reconectar automaticamente. Na Kick, mantenha o webhook HTTPS encaminhando ao app aberto. YouTube e Facebook continuam consultando enquanto a live estiver disponível. Nenhum conector carrega o histórico completo; mensagens podem ser perdidas durante indisponibilidade."
    }
  ]
};

export default content;
