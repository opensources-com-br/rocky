const content = {
  "group": "Avançado",
  "lead": "Ollama é a integração local implementada. Rocky conecta a um servidor existente; não inclui um modelo de IA.",
  "blocks": [
    {
      "type": "h2",
      "text": "Preparar"
    },
    {
      "type": "para",
      "text": "Instale e inicie Ollama seguindo sua documentação. Baixe um modelo de texto, por exemplo com ollama pull llama3.2. Em IA, selecione Ollama local, informe http://localhost:11434 e o nome do modelo; depois teste a conexão."
    },
    {
      "type": "h2",
      "text": "Compatibilidade"
    },
    {
      "type": "para",
      "text": "O app usa a API do Ollama. LM Studio, llama.cpp e servidores com apenas API OpenAI-compatible não são provedores suportados pela interface atual."
    },
    {
      "type": "h2",
      "text": "Recursos da máquina"
    },
    {
      "type": "para",
      "text": "Consumo de memória e tempo de resposta dependem do modelo e do hardware. Teste junto do jogo e encoder antes da live. A documentação não garante desempenho de um tamanho de modelo em uma quantidade específica de RAM."
    },
    {
      "type": "h2",
      "text": "O que fica local"
    },
    {
      "type": "para",
      "text": "Ollama em loopback mantém o processamento da amostra de chat no computador. Transcrição e vozes do sistema também podem ser locais após preparação. A conexão Twitch e os downloads de instalação continuam exigindo internet."
    }
  ]
};

export default content;
