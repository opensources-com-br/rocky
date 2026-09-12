const content = {
  group: "Avançado",
  lead: "Rocky é MIT e vive de contribuição. O repositório aceita código, tradução, vozes, plugins e casos de uso para o showcase.",
  blocks: [
    {"type": "h2", "text": "Rodar em modo de desenvolvimento"},
    {"type": "code", "file": "Terminal", "text": "$ git clone https://github.com/rocky-app/rocky\n$ cd rocky\n$ pnpm install\n$ pnpm tauri dev"},
    {"type": "h2", "text": "Onde ajudar"},
    {"type": "cards", "items": [{"key": "windows / linux", "text": "As portas para outros sistemas são a prioridade atual."}, {"key": "plataformas", "text": "Novos serviços de chat, seguindo a interface de fonte existente."}, {"key": "vozes", "text": "Modelos Piper em mais idiomas e variantes regionais."}, {"key": "tradução", "text": "A interface está em português e inglês."}]},
    {"type": "h2", "text": "Antes de abrir um PR"},
    {"type": "list", "items": ["Uma mudança por PR, com descrição do que muda na experiência de quem transmite.", "Rode pnpm test e pnpm lint antes de enviar.", "Mudanças de interface pedem uma captura antes e depois.", "Novas configurações precisam existir no config.toml e na tela de configurações."]},
    {"type": "h2", "text": "Entrar no showcase"},
    {"type": "para", "text": "Se você transmite com o Rocky, abra um PR adicionando sua configuração ao diretório showcase. Conte o que mudou na sua live, qual modelo e voz você usa, e quantas plataformas mantém conectadas."},
    {"type": "note", "text": "Discussões de arquitetura acontecem nas issues antes do código. Se a mudança é grande, abra uma issue primeiro."},
  ],
};

export default content;
