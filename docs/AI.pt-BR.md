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

A API key permanece na memória e é descartada quando o Rocky fecha. Ela nunca é salva nas preferências. As chamadas usam a [Responses API](https://developers.openai.com/api/reference/resources/responses/methods/create) com `store: false`.

## Comportamento das sugestões

- **Analisar agora** funciona depois que pelo menos uma mensagem real da Twitch chega.
- A análise automática começa desativada. Quando ativada, segue a frequência configurada no agente e só envia um novo lote após mais três mensagens; uma sugestão pendente impede outra automática.
- Cada chamada contém no máximo as 30 mensagens mais recentes e 300 caracteres de cada mensagem.
- O chat é marcado como conteúdo não confiável. Uma sugestão precisa citar IDs presentes na chamada ou será rejeitada pelo Rocky.
- Somente uma análise roda por vez. O Rocky nunca troca automaticamente do Ollama para a OpenAI.
- Sugestões salvas usam as notas locais em SQLite e a exportação para Markdown já existentes.

Pedidos digitados dispensam microfone e whisper.cpp. Uma análise em andamento pode ser cancelada. O teste de conexão gera uma resposta curta com dados fictícios para verificar também a capacidade de geração; provedores por API podem cobrar essa chamada.

Provedores por API recebem o conteúdo selecionado do chat. Confira os controles de dados do provedor antes de ativar a análise automática.
