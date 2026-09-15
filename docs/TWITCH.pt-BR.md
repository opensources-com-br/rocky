# Conexão com a Twitch

O Rocky se conecta ao chat da pessoa autenticada por meio do Device Code Flow oficial da Twitch e da API EventSub WebSocket.

O build oficial pode incluir o Client ID público do Rocky com `-ProckyTwitchClientId=...`. Quando configurado, a pessoa só precisa autorizar sua conta; builds de contribuidores continuam aceitando um Client ID informado nas configurações.

## Configuração

Se o Client ID já aparece configurado, conecte e autorize sua conta. O cadastro abaixo é necessário somente quando o campo está vazio ou você quer usar um cliente próprio.



1. Registre um aplicativo Twitch no [Console de Desenvolvedores](https://dev.twitch.tv/console/apps).
2. Configure-o como cliente público e copie o Client ID. O Rocky nunca precisa do Client Secret.
3. Execute o Rocky e abra **Configurações → Plataformas**.
4. Cole o Client ID e selecione **Conectar Twitch**.
5. Selecione **Abrir Twitch**, entre na Twitch e confirme o código exibido pelo Rocky.
6. Feche as configurações quando o estado mudar para **Conectada**. Novas mensagens do canal autenticado aparecerão na conversa.

O conector solicita apenas `user:read:chat`. O Client ID é salvo nas preferências locais do sistema operacional. Os tokens de acesso e renovação ficam na memória e são descartados quando a pessoa desconecta ou fecha o Rocky.

## Comportamento da conexão

- Os keepalives do EventSub são monitorados enquanto a sessão está ativa.
- URLs de reconexão enviadas pela Twitch são usadas sem recriar as assinaturas.
- Quedas inesperadas geram tentativas após 1, 2, 4, 8, 16 e no máximo 30 segundos.
- Uma nova assinatura é criada após uma reconexão inesperada, e IDs de mensagens repetidas são ignorados.
- O Rocky valida o token OAuth antes de abrir o chat e renova um token expirado quando a autorização da assinatura falha.

Este primeiro conector acompanha o canal da própria pessoa autenticada e consulta a contagem atual de espectadores. Ele ainda não recupera mensagens anteriores, pontos do canal, inscrições ou eventos de apoio pago.

Referências: [OAuth da Twitch](https://dev.twitch.tv/docs/authentication/getting-tokens-oauth/), [validação de tokens](https://dev.twitch.tv/docs/authentication/validate-tokens/) e [EventSub WebSockets](https://dev.twitch.tv/docs/eventsub/handling-websocket-events/).

## Recuperação e ciclo de conexão

Uma migração solicitada pela Twitch mantém o socket anterior até receber as boas-vindas do novo, sem recriar a assinatura do chat. Mensagens repetidas são filtradas durante a sessão; eventos de sockets aposentados são ignorados. Um socket aberto que não recebe boas-vindas em 20 segundos entra no fluxo de reconexão com espera progressiva.

Desconectar cancela tarefas de autenticação, consultas de rede, reconexões agendadas e tentativas de abertura do socket. Fechar o cliente é idempotente e impede novas conexões. Falhas de audiência mantêm o último valor recebido. Mensagens WebSocket fragmentadas têm limite de 1.048.576 caracteres.

A deduplicação não recupera mensagens perdidas: interrupções de rede ainda podem causar lacunas no chat, pois a Twitch não reproduz os eventos desse período.
