# Conexão com a Kick

O Rocky conecta o canal autenticado usando as APIs oficiais OAuth 2.1 e Events da Kick. Ele recebe novos eventos `chat.message.sent` e a contagem atual de espectadores. Não recupera histórico nem envia mensagens.

## Requisitos

- um aplicativo no Kick Dev com Client ID e Client Secret;
- o callback OAuth `http://localhost:18181/oauth/kick/callback`;
- uma URL pública HTTPS que encaminhe as requisições de webhook, sem alterações, para `http://localhost:18181/webhooks/kick` enquanto o Rocky estiver aberto.

A Kick entrega eventos de chat somente por webhook. Um endereço local do computador não pode ser acessado pela Kick, por isso a captura exige um endpoint HTTPS ou túnel cadastrado no aplicativo do Kick Dev. Encaminhe o corpo bruto e todos os cabeçalhos `Kick-Event-*` sem modificações.

## Conectar

1. Abra **Configurações → Plataformas → Kick → Criar app**.
2. Cadastre no Kick Dev o callback e o webhook descritos acima.
3. Copie o Client ID e o Client Secret para o Rocky. Altere o callback local somente se cadastrar exatamente o mesmo valor no Kick Dev.
4. Selecione **Conectar Kick**, depois **Abrir Kick**, e autorize as permissões solicitadas.
5. Mantenha o encaminhamento HTTPS ativo durante a live. O Rocky muda para **Conectada** depois de criar a assinatura do chat.

O Rocky solicita `user:read`, `channel:read` e `events:subscribe`. O Client Secret fica no Keychain do macOS ou protegido pelo DPAPI do usuário no Windows. Tokens de acesso e renovação permanecem na memória e são descartados ao fechar o app.

## Tratamento dos eventos

O Rocky verifica cada webhook com a chave RSA publicada pela Kick, o horário, o identificador e o corpo bruto. Eventos repetidos são confirmados e ignorados. Ao desconectar, o app remove a assinatura ativa quando a Kick está acessível.

Se o callback OAuth funcionar, mas nenhuma mensagem chegar, verifique primeiro o webhook público e o encaminhamento HTTPS. O Rocky não recupera mensagens enviadas enquanto o webhook ou o app estava indisponível.

Referências: [OAuth da Kick](https://github.com/KickEngineering/KickDevDocs/blob/main/getting-started/generating-tokens-oauth2-flow.md), [assinatura de eventos](https://github.com/KickEngineering/KickDevDocs/blob/main/events/subscribe-to-events.md) e [segurança dos webhooks](https://github.com/KickEngineering/KickDevDocs/blob/main/events/webhook-security.md).
