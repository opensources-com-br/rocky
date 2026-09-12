const content = {
  group: "Configuração",
  lead: "Conecte um ou mais canais em Configurações → Plataformas. O token de cada serviço fica no Keychain do seu Mac e nenhuma mensagem passa por servidor de terceiros.",
  blocks: [
    {"type": "h2", "text": "O que Rocky lê de cada serviço"},
    {"type": "table", "cols": "minmax(0,1fr) minmax(0,1.7fr) minmax(0,1fr)", "head": ["PLATAFORMA", "O QUE ROCKY LÊ", "AUTENTICAÇÃO"], "rows": [["Twitch", "Chat, bits, raids e subs", "OAuth"], ["Kick", "Chat e presentes", "OAuth"], ["YouTube", "Chat ao vivo e superchats", "OAuth"], ["Facebook", "Chat e estrelas", "Token da página"]]},
    {"type": "h2", "text": "Ligar e desligar durante a live"},
    {"type": "para", "text": "Os chips no topo da janela ligam e desligam cada fonte sem desconectar a conta. Desligar uma plataforma remove suas mensagens da fila e tira seus espectadores da contagem, útil quando um canal secundário começa a fazer barulho."},
    {"type": "h2", "text": "Escrever no chat"},
    {"type": "para", "text": "Rocky só envia mensagem quando você pede em voz alta — “manda o link no chat”. A permissão de escrita é opcional e pode ser negada por plataforma; sem ela, o app só lê."},
    {"type": "note", "text": "Contas com autenticação de dois fatores funcionam normalmente. O fluxo de OAuth abre no seu navegador padrão e volta para o app."},
    {"type": "h2", "text": "Configuração por arquivo"},
    {"type": "code", "file": "~/.rocky/config.toml", "text": "[[platforms]]\nkind    = \"twitch\"\nchannel = \"seucanal\"\nwrite   = true\n\n[[platforms]]\nkind    = \"youtube\"\nchannel = \"UC...\"\nwrite   = false"},
  ],
};

export default content;
