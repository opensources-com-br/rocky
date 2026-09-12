# Conectar o YouTube

O Rocky lê novas mensagens do chat ao vivo e a contagem atual de espectadores de uma transmissão ativa do canal autorizado. Ele não envia mensagens e ainda não transforma Super Chats ou Super Stickers em eventos da aba de apoios.

## Preparar as credenciais

1. Abra o [Google Cloud Console](https://console.cloud.google.com/apis/credentials) e selecione ou crie um projeto.
2. Ative a **YouTube Data API v3**.
3. Configure a tela de consentimento OAuth. Se o aplicativo estiver em teste, adicione a conta do canal como usuário de teste.
4. Crie um cliente OAuth do tipo **Aplicativo para computador**.
5. Copie o Client ID e o Client Secret exibidos pelo Google.

## Conectar uma live

1. Inicie uma transmissão pública ou não listada com chat ao vivo habilitado.
2. No Rocky, abra **Configurações → Plataformas**.
3. Informe o Client ID e o Client Secret no cartão YouTube e use **Conectar YouTube**.
4. Autorize a conta no navegador. O Google retorna ao callback local exibido pelo Rocky.
5. Aguarde o estado **Conectada** e envie uma mensagem por outra conta para validar o chat.

O Rocky pede acesso somente para leitura do YouTube. A autorização e o refresh token permanecem na memória da sessão; o Client Secret é salvo no cofre do sistema. O conector procura a transmissão ativa do canal, respeita o intervalo de consulta retornado pela API e não recupera o histórico completo da live.

Se nenhuma live for encontrada, confirme que a transmissão pertence à conta autorizada, está ativa e tem chat habilitado. Se a autorização não voltar ao app, confira se a porta `18182` está livre e tente novamente.
