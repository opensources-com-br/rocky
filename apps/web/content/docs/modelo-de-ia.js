const content = {
  "group": "Configuração",
  "lead": "Escolha o provedor em Configurações → IA. A integração atual oferece Ollama local, OpenAI API e OpenRouter.",
  "blocks": [
    {
      "type": "h2",
      "text": "Ollama"
    },
    {
      "type": "para",
      "text": "Mantenha o servidor e um modelo de texto instalados. Use http://localhost:11434 e informe o nome exato do modelo; o padrão do app é llama3.2. Rocky não instala modelos de IA nem detecta uma lista automaticamente."
    },
    {
      "type": "h2",
      "text": "OpenAI e OpenRouter"
    },
    {
      "type": "para",
      "text": "OpenAI usa https://api.openai.com e a API Responses. OpenRouter usa https://openrouter.ai/api; o modelo padrão é openrouter/free, cuja disponibilidade varia. Informe uma chave e um modelo disponível na sua conta. Não há seletor Anthropic ou integração genérica OpenAI-compatible."
    },
    {
      "type": "h2",
      "text": "Testar e salvar"
    },
    {
      "type": "para",
      "text": "Teste a conexão antes da live. O teste faz uma geração curta e pode consumir uso do provedor. Salvar configuração e chave grava a credencial no Keychain do macOS ou protegida por DPAPI no Windows; edições não salvas ficam na memória. Trocar provedor ou endpoint limpa a chave atual."
    },
    {
      "type": "h2",
      "text": "Contexto e continuidade"
    },
    {
      "type": "para",
      "text": "As análises usam até 200 mensagens recebidas nos últimos dois minutos, com até 300 caracteres por mensagem. Os últimos quatro pares de pergunta e resposta ajudam na continuidade. Não há janela configurável de dez minutos ou da live inteira. Fontes citadas precisam pertencer à amostra enviada."
    },
    {
      "type": "h2",
      "text": "Uso e privacidade"
    },
    {
      "type": "para",
      "text": "Ollama em loopback processa as sugestões no computador. OpenAI/OpenRouter recebem o pedido e a amostra de chat; OpenRouter também encaminha ao provedor do modelo. Totais de tokens são parciais, não uma estimativa de cobrança. Cancelar não desfaz consumo já ocorrido."
    }
  ]
};

export default content;
