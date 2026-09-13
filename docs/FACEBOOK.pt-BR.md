# Facebook Live

O Rocky recebe novos comentários e a audiência informada pela Graph API em uma live ativa de uma Página administrada pela conta autorizada.

## Configurar o aplicativo

1. Abra [Meta for Developers](https://developers.facebook.com/apps/) e crie um aplicativo do tipo Business.
2. Adicione o produto Login do Facebook.
3. Cadastre exatamente `http://127.0.0.1:18183/oauth/facebook/callback` como URI de redirecionamento OAuth válida.
4. Garanta que o aplicativo possa solicitar `pages_show_list` e `pages_read_engagement`. Contas fora das funções do aplicativo podem exigir modo Live e aprovação dessas permissões pela Meta.
5. Em Configurações → Plataformas → Facebook, informe App ID e App Secret.

O App Secret é salvo no Keychain do macOS ou protegido pelo DPAPI do usuário no Windows. Tokens de usuário e de Página permanecem apenas na memória e são descartados ao desconectar ou fechar o Rocky.

## Conectar a live

1. Inicie uma live com comentários habilitados em uma Página administrada pela conta.
2. Clique em **Conectar Facebook** e depois em **Abrir Facebook**.
3. Autorize as Páginas que o Rocky poderá consultar.
4. O Rocky procura a primeira Página autorizada com live ativa e começa a consultar novos comentários.

O conector não recupera o histórico completo, não publica no chat e não processa Estrelas ou outras contribuições pagas. Se nenhuma Página autorizada estiver ao vivo, a conexão informa a falha e pode ser refeita depois que a transmissão começar.
