# Conectar o TikTok LIVE

O TikTok não oferece chat de LIVE e audiência pela API pública para desenvolvedores. Por isso, a versão para PC do Rocky se conecta ao WebCast público da live usando a biblioteca comunitária TikTokLiveJava e o serviço de assinatura Eulerstream.

## Conectar

1. Inicie uma LIVE pública no TikTok.
2. No Rocky, abra **Configurações > Plataformas > TikTok LIVE**.
3. Informe o `@usuário` da conta que está transmitindo.
4. Use **Conectar TikTok**.

O Rocky não solicita senha, cookie, token ou Client Secret do TikTok. Apenas o nome de usuário fica salvo nas preferências locais. A conexão envia o nome de usuário e o identificador público da sala ao TikTok e ao serviço Eulerstream, necessários para abrir o WebSocket da live.

## Recuperação da conexão

O Rocky gerencia as tentativas de conexão em segundo plano. Cada tentativa tem um limite de 20 segundos para estabelecer a conexão. Em falhas temporárias ou quedas do chat, tenta novamente após 2, 4, 8, 16 e 30 segundos, com no máximo cinco novas tentativas consecutivas. Uma conexão bem-sucedida reinicia esse limite. A biblioteca não executa um segundo ciclo de reconexão em paralelo.

Durante a recuperação, o status informa a tentativa e o tempo de espera. Comentários repetidos são filtrados mesmo ao reconectar à mesma sala; eventos de tentativas antigas são ignorados. Não há recuperação garantida das mensagens enviadas durante uma queda.

Conta identificada como offline/inexistente ou evento de LIVE encerrada termina a tentativa sem repetir automaticamente. Se o serviço não identificar a causa, o Rocky aplica as tentativas limitadas e apresenta falha ao esgotá-las. Use **Conectar TikTok** novamente depois de conferir a live. **Desconectar** ou encerrar o Rocky cancela as novas tentativas.

Erros mostrados na interface não incluem respostas brutas, URLs privadas ou credenciais do serviço. O controle de reconexão foi testado com transportes simulados; a latência e a disponibilidade reais ainda dependem do TikTok e do Eulerstream.

## Eventos recebidos

O conector recebe novos comentários e atualizações de espectadores da live atual. Presentes, curtidas, seguidores, assinaturas, eventos pagos e envio de mensagens não entram no Rocky.

Essa integração usa um protocolo não oficial e pode parar de funcionar quando o TikTok alterar o WebCast. Se a conta não estiver ao vivo, for privada, tiver restrição etária/regional ou o serviço de conexão estiver indisponível, o Rocky mostrará falha na conexão.
