# Facebook Live

O Rocky recebe novos comentários e a audiência informada pela Graph API em uma live ativa de uma Página administrada pela conta autorizada.

## Configurar o aplicativo

1. Abra [Meta for Developers](https://developers.facebook.com/apps/) e crie um aplicativo do tipo Business.
2. Adicione o produto Login do Facebook.
3. Cadastre exatamente `http://127.0.0.1:18183/oauth/facebook/callback` como URI de redirecionamento OAuth válida.
4. Garanta que o aplicativo possa solicitar `pages_show_list`, `pages_read_engagement` e `pages_read_user_content`. Contas fora das funções do aplicativo podem exigir modo Live e aprovação dessas permissões pela Meta.
5. Em Configurações → Plataformas → Facebook, informe App ID e App Secret.

O App Secret é salvo no Keychain do macOS ou protegido pelo DPAPI do usuário no Windows. Tokens de usuário e de Página permanecem apenas na memória e são descartados ao desconectar ou fechar o Rocky.

## Conectar a live

1. Inicie uma live com comentários habilitados em uma Página administrada pela conta.
2. Clique em **Conectar Facebook** e depois em **Abrir Facebook**.
3. Autorize as Páginas que o Rocky poderá consultar.
4. O Rocky procura a primeira Página autorizada com live ativa e começa a consultar novos comentários.

O conector não recupera o histórico completo, não publica no chat e não processa Estrelas ou outras contribuições pagas. Se nenhuma Página autorizada estiver ao vivo, a conexão informa a falha e pode ser refeita depois que a transmissão começar.

## Recuperação e consultas

Comentários são consultados a cada 2 segundos quando chegam mensagens novas; sem novidades, o intervalo sobe gradualmente até 5 segundos. A audiência é consultada a cada 30 segundos e mantém o último valor recebido se uma consulta falhar.

Falhas temporárias de rede ou da API durante a consulta de comentários têm até 5 novas tentativas, com esperas de 2, 4, 8, 16 e 30 segundos. O `Retry-After` em segundos é respeitado até 5 minutos. Uma consulta bem-sucedida reinicia esse limite. Erros de token ou permissão encerram a conexão e exigem uma nova autorização.

Desconectar cancela consultas agendadas e interrompe requisições em andamento. Eventos de sessões anteriores são descartados. A consulta continua limitada aos 100 comentários mais recentes: chats muito movimentados podem ultrapassar essa janela entre consultas.
