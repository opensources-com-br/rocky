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
      "text": "Quando uma versão estiver disponível, clique em Atualizar ou abra Configurações → Dados → Baixar atualização. Rocky baixa o pacote da sua arquitetura, mostra o progresso e verifica o SHA-256. Depois, desconecte as plataformas e clique em Atualizar e reiniciar. O aplicativo instala a atualização e reabre, preservando seus dados, preferências e credenciais salvas. No macOS, instale Rocky em /Applications/Rocky.app ou ~/Applications/Rocky.app, com permissão de escrita na pasta; sem essa permissão, use o instalador oficial. No Windows, o sistema pode pedir autorização para instalar a atualização."
    },
    {
      "type": "para",
      "text": "Se sua versão antiga só oferece Abrir instalador, instale uma vez a release com atualização integrada sobre a instalação existente, sem desinstalar nem apagar dados. A partir dessa versão, use Atualizar e reiniciar. Execuções pelo código usam a página de releases para instalar o aplicativo empacotado. Se houver falha, confira a mensagem em Dados e tente novamente."
    },
    {
      "type": "h2",
      "text": "Atualização do site"
    },
    {
      "type": "para",
      "text": "Este site contém apresentação, documentação e showcase; o Rocky roda no aplicativo desktop. Quando uma nova versão do site for publicada, um aviso oferece Recarregar site ou Depois. A atualização só recarrega a página quando você clicar e preserva o idioma escolhido."
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
