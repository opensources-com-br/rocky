const content = {
  group: "Avançado",
  lead: "Rodar o modelo na sua máquina deixa a live inteira offline: nenhuma mensagem do seu chat sai do computador, e não há custo por token.",
  blocks: [
    {"type": "h2", "text": "Ollama"},
    {"type": "code", "file": "Terminal", "text": "$ brew install ollama\n$ ollama pull llama3.1:8b\n$ ollama serve"},
    {"type": "para", "text": "Com o servidor rodando, escolha ollama em Configurações → IA. O app detecta os modelos disponíveis automaticamente."},
    {"type": "h2", "text": "LM Studio e servidores compatíveis"},
    {"type": "para", "text": "Qualquer servidor que exponha a API no formato OpenAI funciona com provider openai-compat. Basta apontar a base_url e deixar a chave em branco."},
    {"type": "h2", "text": "Qual modelo escolher"},
    {"type": "table", "cols": "minmax(0,1fr) minmax(0,1fr) minmax(0,1.4fr)", "head": ["TAMANHO", "MEMÓRIA", "COMPORTAMENTO"], "rows": [["3B", "~4 GB", "Rápido; bom para contar e agrupar, fraco em nuance"], ["8B", "~8 GB", "Recomendado; equilibra qualidade e folga para o encoder"], ["14B+", "16 GB+", "Melhor redação; exige máquina dedicada ao lado da live"]]},
    {"type": "note", "text": "Transmitir e inferir na mesma máquina disputa CPU com o encoder. Se a queda de quadros subir, baixe o tamanho do modelo antes de mexer no bitrate."},
    {"type": "h2", "text": "Sem internet nenhuma"},
    {"type": "para", "text": "Com provider local e engine de voz local, o Rocky funciona sem conexão — exceto, claro, pela própria ingestão do chat, que vem das plataformas."},
  ],
};

export default content;
