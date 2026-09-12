const content = {
  "group": "Começando",
  "lead": "Use os instaladores alpha para macOS e Windows disponíveis na página de releases do repositório opensources-com-br/rocky. Eles são builds de desenvolvimento não assinados.",
  "blocks": [
    {
      "type": "h2",
      "text": "Instaladores"
    },
    {
      "type": "para",
      "text": "No macOS, use o DMG compatível com sua máquina; no Windows, MSI ou EXE. Confira os arquivos e checksums SHA-256 da release escolhida. O sistema pode mostrar avisos de segurança por falta de assinatura. Não há distribuição Homebrew do Rocky documentada neste projeto."
    },
    {
      "type": "h2",
      "text": "Executar pelo código"
    },
    {
      "type": "para",
      "text": "Com Git e JDK 17 instalados, clone https://github.com/opensources-com-br/rocky.git. Na raiz, execute ./gradlew :apps:desktop:run no macOS ou .\\gradlew.bat :apps:desktop:run no PowerShell. O desktop usa Kotlin e Compose Multiplatform; Node.js é usado pela web."
    },
    {
      "type": "h2",
      "text": "Permissões"
    },
    {
      "type": "para",
      "text": "Voz é opcional e requer acesso ao microfone. Atalhos globais dependem das permissões e disponibilidade do sistema; confira o estado de registro nas configurações de Voz. É possível começar usando somente texto."
    },
    {
      "type": "h2",
      "text": "Sistemas"
    },
    {
      "type": "para",
      "text": "O projeto empacota macOS e Windows. Não há instalador Linux oferecido pelo fluxo atual. Consulte os requisitos e a arquitetura do artefato escolhido antes de instalar."
    }
  ]
};

export default content;
