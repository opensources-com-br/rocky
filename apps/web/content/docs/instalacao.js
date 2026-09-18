const content = {
  "group": "Começando",
  "lead": "Instale Rocky para macOS ou Windows pela página de releases de opensources-com-br/rocky. As versões com atualização integrada permitem instalar as próximas versões pelo próprio aplicativo.",
  "blocks": [
    {
      "type": "h2",
      "text": "Instaladores"
    },
    {
      "type": "para",
      "text": "No macOS, escolha o DMG arm64 para Apple Silicon ou x86_64 para Intel e copie Rocky para Aplicativos antes de abrir. No Windows, use MSI ou EXE amd64/x64. O botão de download usa a arquitetura identificada pelo navegador; se não puder identificá-la, abre a página oficial para você escolher. Confira os checksums SHA-256 da release."
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
      "text": "Voz é opcional e requer acesso ao microfone. Atalhos globais dependem das permissões e disponibilidade do sistema; confira o estado de registro nas configurações de Voz. É possível começar pelas ações rápidas de análise sem configurar microfone."
    },
    {
      "type": "h2",
      "text": "Atualizar e reiniciar"
    },
    {
      "type": "para",
      "text": "Depois de tentar abrir Rocky, acesse Ajustes do Sistema → Privacidade e Segurança, localize o aviso do Rocky e clique em Abrir Mesmo Assim. Use essa exceção somente se você baixou o DMG da release oficial e conferiu o SHA-256 publicado. A próxima release gerada com as credenciais de distribuição será assinada e notarizada."
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
