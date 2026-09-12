const content = {
  group: "Começando",
  lead: "Rocky é um aplicativo desktop que roda inteiramente na sua máquina. Não há conta para criar nem chave da nossa parte — você instala, conecta seus canais e escolhe qual IA ele vai usar.",
  blocks: [
    {"type": "note", "text": "Requer macOS 13 ou superior. As versões para Windows e Linux estão em construção — acompanhe pelo repositório."},
    {"type": "h2", "text": "Homebrew"},
    {"type": "para", "text": "A forma recomendada. Atualiza junto com o resto do seu sistema."},
    {"type": "code", "file": "Terminal", "text": "$ brew install --cask rocky\n$ open -a Rocky"},
    {"type": "h2", "text": "Arquivo .dmg"},
    {"type": "para", "text": "Baixe o .dmg mais recente na página de releases, arraste o Rocky para a pasta Aplicativos e abra. Na primeira execução o macOS pede confirmação porque o app é distribuído fora da App Store."},
    {"type": "h2", "text": "Compilar a partir do código"},
    {"type": "code", "file": "Terminal", "text": "$ git clone https://github.com/rocky-app/rocky\n$ cd rocky\n$ pnpm install\n$ pnpm tauri build"},
    {"type": "h2", "text": "Permissões pedidas na primeira abertura"},
    {"type": "table", "cols": "minmax(0,1fr) minmax(0,1.6fr)", "head": ["PERMISSÃO", "PARA QUÊ"], "rows": [["Microfone", "Ouvir as suas perguntas por voz"], ["Acessibilidade", "Registrar o atalho global de fala"], ["Rede local", "Falar com um modelo de IA rodando na sua máquina"]]},
    {"type": "para", "text": "Nenhuma dessas permissões envia dados para fora. Você pode revogar qualquer uma nas Preferências do Sistema e o resto do app continua funcionando."},
  ],
};

export default content;
