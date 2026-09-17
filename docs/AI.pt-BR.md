# Provedores de IA

O Rocky pode gerar sugestões fundamentadas no chat da Twitch, Kick, YouTube, Facebook ou TikTok LIVE usando Ollama local, a Responses API da OpenAI, a Messages API da Anthropic, a API Gemini do Google, a Responses API do xAI Grok ou OpenRouter.

## Ollama

1. [Instale o Ollama](https://ollama.com/download) e mantenha-o em execução.
2. Baixe o modelo padrão com `ollama pull llama3.2` ou escolha outro modelo de texto instalado.
3. No Rocky, abra **Configurações → IA** e selecione **Ollama local**.
4. Mantenha `http://localhost:11434` como endereço, informe o modelo instalado e selecione **Testar conexão**.

Para manter a análise no computador, use um modelo local no Ollama; somente o endereço loopback não comprova onde o servidor executa o modelo. O Rocky não instala nem inclui modelos no aplicativo. Use Buscar modelos e Escolher modelo em IA, ou informe o nome manualmente.

## API da OpenAI

1. Crie uma API key para seu projeto na OpenAI API. Uma assinatura do ChatGPT não fornece API key nem créditos de uso da API.
2. Em **Configurações → IA**, selecione **OpenAI API**.
3. Mantenha `https://api.openai.com` como endereço, informe um modelo disponível no projeto e cole a API key.
4. Selecione **Testar conexão**.

Use **Salvar configuração e chave** para persistir a chave no Keychain (macOS) ou protegida por DPAPI do usuário (Windows). A chave não é salva em preferências comuns. **Apagar chave** remove a credencial salva; edições não salvas ficam apenas na memória. As chamadas usam a [Responses API](https://developers.openai.com/api/reference/resources/responses/methods/create) com `store: false` e uma chave estável de cache para requisições elegíveis.

## OpenRouter

1. Crie uma API key nas [configurações do OpenRouter](https://openrouter.ai/settings/keys).
2. Em **Configurações → IA**, selecione **OpenRouter**.
3. Mantenha `https://openrouter.ai/api` como endereço e `openrouter/free` como modelo.
4. Cole a API key e selecione **Testar conexão**.

`openrouter/free` escolhe automaticamente um modelo gratuito compatível. A disponibilidade, o modelo escolhido e os limites podem variar. As mensagens selecionadas são processadas pelo OpenRouter e pelo provedor do modelo escolhido. A persistência usa o mesmo cofre descrito acima.

## API da Anthropic

1. Crie uma API key no [Console da Anthropic](https://console.anthropic.com/settings/keys). Uma assinatura Claude não fornece uma API key nem créditos de uso da API.
2. Em **Configurações → IA**, selecione **Anthropic API**.
3. Mantenha `https://api.anthropic.com` como endereço e use `claude-haiku-4-5-20251001` ou outro modelo disponível na sua conta.
4. Cole a API key e selecione **Testar conexão**.

O Rocky usa a [Messages API](https://docs.anthropic.com/en/api/messages) com a chave informada. As mensagens selecionadas do chat e o pedido são enviados à Anthropic; teste de conexão também gera uma resposta curta e pode ter cobrança. A chave usa o mesmo cofre do sistema descrito acima.

## API Google Gemini

1. Crie uma API key no [Google AI Studio](https://aistudio.google.com/app/apikey). Uma assinatura do app Gemini não fornece uma API key nem créditos de uso da API.
2. Em **Configurações → IA**, selecione **Google Gemini API**.
3. Mantenha `https://generativelanguage.googleapis.com` como endereço e use `gemini-3.8-flash` ou outro modelo disponível na sua conta.
4. Cole a API key e selecione **Testar conexão**.

O Rocky usa `generateContent` da [API Gemini](https://ai.google.dev/api/generate-content) com JSON Schema. Bloqueios e respostas incompletas são identificados, e o consumo informado inclui tokens de raciocínio. As mensagens selecionadas e o pedido são enviados ao Google; o teste também gera uma resposta curta e pode ter cobrança.

## API xAI Grok

1. Crie uma API key no [Console xAI](https://console.x.ai/).
2. Em **Configurações → IA**, selecione **xAI Grok API**.
3. Mantenha `https://api.x.ai` como endereço e use `grok-4.6` ou outro modelo disponível na sua conta.
4. Cole a API key e selecione **Testar conexão**.

O Rocky usa a [Responses API do xAI](https://docs.x.ai/developers/rest-api-reference/inference/responses) com saída estruturada, armazenamento remoto desativado e cache de prompt. Modelos Grok 4.5 e 4.6 usam raciocínio baixo para reduzir latência e custo; o consumo inclui todos os tokens informados. Apenas modelos de linguagem são listados. Uma assinatura Grok não fornece credenciais nem créditos da API. As mensagens selecionadas e o pedido são enviados ao xAI; o teste também gera uma resposta curta e pode ter cobrança.

## Comportamento das sugestões

- **Analisar agora** funciona depois que pelo menos uma mensagem real da Twitch, Kick, YouTube, Facebook ou TikTok LIVE chega.
- A análise automática começa desativada. Quando ativada, segue o perfil escolhido em IA (Discreto: cinco minutos; Proativo: dois minutos) e só envia um novo lote após mais três mensagens.
- Cada chamada usa até 200 mensagens recebidas nos últimos dois minutos e até 300 caracteres de cada mensagem. É uma amostra limitada; quedas podem causar lacunas.
- O chat é marcado como conteúdo não confiável. Com mensagens na amostra, uma sugestão precisa citar ao menos um ID válido. Pedidos diretos sem mensagens podem responder sem fontes.
- Somente uma análise roda por vez. O Rocky nunca troca automaticamente de provedor.
- Sugestões salvas usam as notas locais em SQLite e a exportação para Markdown já existentes.

As ações Dúvidas principais, O que perdi? e Ideias do chat dispensam microfone e whisper.cpp. O campo de pergunta digitada está oculto no layout atual; use voz para pedidos livres. Uma análise em andamento pode ser cancelada. O teste de conexão gera uma resposta curta com dados fictícios para verificar também a capacidade de geração; provedores por API podem cobrar essa chamada.

Provedores por API recebem o conteúdo selecionado do chat. Confira os controles de dados do provedor antes de ativar a análise automática.

APIs remotas exigem HTTPS; HTTP é aceito somente no Ollama em loopback. Trocar provedor ou endereço limpa a chave atual: informe a credencial correta e salve novamente.

Em Conversa → Amostra · detalhes, consulte a duração da última análise e os tokens informados. A contagem é parcial: falhas, cancelamentos e testes de conexão podem não informar uso. O OpenRouter faz uma geração por análise. Os contadores não estimam faturamento e cancelar não reverte cobranças.
