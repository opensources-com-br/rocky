const content = {
  group: "Configuração",
  lead: "Rocky não intermedeia nada. Você cola a chave do provedor que já usa, ou aponta para um modelo rodando na sua máquina — e paga direto ao provedor, ou nada.",
  blocks: [
    {"type": "h2", "text": "Provedores suportados"},
    {"type": "table", "cols": "minmax(0,1fr) minmax(0,1fr) minmax(0,1.4fr)", "head": ["PROVIDER", "ONDE RODA", "OBSERVAÇÃO"], "rows": [["anthropic", "Remoto", "Requer chave; melhor qualidade de síntese"], ["openai", "Remoto", "Requer chave"], ["ollama", "Local", "Sem chave, sem internet"], ["openai-compat", "Local ou remoto", "Qualquer servidor com API compatível"]]},
    {"type": "h2", "text": "Arquivo de configuração"},
    {"type": "para", "text": "Tudo que a tela de configurações faz pode ser escrito à mão. O arquivo é lido a cada alteração, sem precisar reiniciar o app."},
    {"type": "code", "file": "~/.rocky/config.toml", "text": "[ai]\n# anthropic | openai | ollama | openai-compat\nprovider = \"ollama\"\nmodel    = \"llama3.1:8b\"\nbase_url = \"http://localhost:11434\"\n# api_key = \"sk-...\"   (provedores remotos)\n\n[agent]\ntone      = \"direto\"\ninterrupt = false\ninterventions_per_10min = 3\nread_superchats = true"},
    {"type": "h2", "text": "Janela de contexto"},
    {"type": "para", "text": "Quanto do chat o Rocky considera ao formular uma fala. Janelas curtas reagem rápido ao que está acontecendo agora; janelas longas percebem padrões que atravessam a live inteira, e custam mais tokens."},
    {"type": "cards", "items": [{"key": "2 min", "text": "Reação imediata. Bom para chat muito rápido."}, {"key": "10 min", "text": "Padrão. Equilibra reação e memória."}, {"key": "live inteira", "text": "Percebe promessas do começo da transmissão."}]},
    {"type": "note", "text": "Com provedor local, o custo por token é zero e a janela longa só pesa em memória. Um modelo de 8B roda confortavelmente ao lado de um encoder de transmissão em máquinas com 16 GB."},
  ],
};

export default content;
