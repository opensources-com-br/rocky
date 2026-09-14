# Conectar o TikTok LIVE

O TikTok não oferece chat de LIVE e audiência pela API pública para desenvolvedores. Por isso, a versão para PC do Rocky se conecta ao WebCast público da live usando a biblioteca comunitária TikTokLiveJava e o serviço de assinatura Eulerstream.

## Conectar

1. Inicie uma LIVE pública no TikTok.
2. No Rocky, abra **Configurações > Plataformas > TikTok LIVE**.
3. Informe o `@usuário` da conta que está transmitindo.
4. Use **Conectar TikTok**.

O Rocky não solicita senha, cookie, token ou Client Secret do TikTok. Apenas o nome de usuário fica salvo nas preferências locais. A conexão envia o nome de usuário e o identificador público da sala ao TikTok e ao serviço Eulerstream, necessários para abrir o WebSocket da live.

## Escopo atual

O conector recebe novos comentários e atualizações de espectadores da live atual. Presentes, curtidas, seguidores, assinaturas, eventos pagos e envio de mensagens não entram no Rocky.

Essa integração usa um protocolo não oficial e pode parar de funcionar quando o TikTok alterar o WebCast. Se a conta não estiver ao vivo, for privada, tiver restrição etária/regional ou o serviço de conexão estiver indisponível, o Rocky mostrará falha na conexão.
