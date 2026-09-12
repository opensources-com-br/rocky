# Provedores de IA

O Rocky pode gerar sugestões fundamentadas no chat da Twitch usando um modelo local do Ollama ou a Responses API da OpenAI.

## Ollama

1. [Instale o Ollama](https://ollama.com/download) e mantenha-o em execução.
2. Baixe o modelo padrão com `ollama pull llama3.2` ou escolha outro modelo de texto instalado.
3. No Rocky, abra **Configurações → IA** e selecione **Ollama local**.
4. Mantenha `http://localhost:11434` como endereço, informe o modelo instalado e selecione **Testar conexão**.

As mensagens permanecem no computador quando o endereço do Ollama é local. O Rocky não instala nem inclui modelos no aplicativo.

## API da OpenAI

1. Crie uma API key para seu projeto na OpenAI API. Uma assinatura do ChatGPT não fornece API key nem créditos de uso da API.
2. Em **Configurações → IA**, selecione **OpenAI API**.
3. Mantenha `https://api.openai.com` como endereço, informe um modelo disponível no projeto e cole a API key.
4. Selecione **Testar conexão**.

Use **Salvar configuração e chave** para persistir a chave no Keychain (macOS) ou protegida por DPAPI do usuário (Windows). A chave não é salva em preferências comuns. **Apagar chave** remove a credencial salva; edições não salvas ficam apenas na memória. As chamadas usam a [Responses API](https://developers.openai.com/api/reference/resources/responses/methods/create) com `store: false`.

## OpenRouter

1. Crie uma API key nas [configurações do OpenRouter](https://openrouter.ai/settings/keys).
2. Em **Configurações → IA**, selecione **OpenRouter**.
3. Mantenha `https://openrouter.ai/api` como endereço e `openrouter/free` como modelo.
4. Cole a API key e selecione **Testar conexão**.

`openrouter/free` escolhe automaticamente um modelo gratuito compatível. A disponibilidade, o modelo escolhido e os limites podem variar. As mensagens selecionadas são processadas pelo OpenRouter e pelo provedor do modelo escolhido. A persistência usa o mesmo cofre descrito acima.

## Comportamento das sugestões

- **Analisar agora** funciona depois que pelo menos uma mensagem real da Twitch chega.
- A análise automática começa desativada. Quando ativada, segue a frequência configurada no agente e só envia um novo lote após mais três mensagens.
- Cada chamada usa até 200 mensagens recebidas nos últimos dois minutos e até 300 caracteres de cada mensagem. É uma amostra limitada; quedas podem causar lacunas.
- O chat é marcado como conteúdo não confiável. Uma sugestão precisa citar IDs presentes na chamada ou será rejeitada pelo Rocky.
- Somente uma análise roda por vez. O Rocky nunca troca automaticamente do Ollama para a OpenAI.
- Sugestões salvas usam as notas locais em SQLite e a exportação para Markdown já existentes.

Pedidos digitados dispensam microfone e whisper.cpp. Uma análise em andamento pode ser cancelada. O teste de conexão gera uma resposta curta com dados fictícios para verificar também a capacidade de geração; provedores por API podem cobrar essa chamada.

Provedores por API recebem o conteúdo selecionado do chat. Confira os controles de dados do provedor antes de ativar a análise automática.

APIs remotas exigem HTTPS; HTTP é aceito somente no Ollama em loopback. Trocar provedor ou endereço limpa a chave atual: informe a credencial correta e salve novamente.

Conversa mostra duração da última análise e tokens informados. A contagem é parcial: falhas, cancelamentos, testes de conexão e tentativas inválidas podem não informar uso. OpenRouter pode tentar até três respostas quando o formato é inválido. Os contadores não estimam faturamento e cancelar não reverte cobranças.
