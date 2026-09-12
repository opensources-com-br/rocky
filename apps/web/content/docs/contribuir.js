const content = {
  "group": "Avançado",
  "lead": "O projeto é MIT e usa Kotlin Multiplatform no desktop e Next.js na web.",
  "blocks": [
    {
      "type": "h2",
      "text": "Executar"
    },
    {
      "type": "para",
      "text": "Clone https://github.com/opensources-com-br/rocky.git. Com JDK 17, execute ./gradlew :apps:desktop:run na raiz (gradlew.bat no Windows). Para a web, entre em apps/web, execute npm ci e npm run dev."
    },
    {
      "type": "h2",
      "text": "Validar"
    },
    {
      "type": "list",
      "items": [
        "Desktop: ./gradlew build, ou .\\gradlew.bat build no Windows.",
        "Web: npm run lint, npm test e npm run build dentro de apps/web.",
        "Faça commits pequenos e descreva mudanças na experiência. Para alterações visuais, inclua capturas e verifique tamanhos de janela."
      ]
    },
    {
      "type": "h2",
      "text": "Relatos de uso"
    },
    {
      "type": "para",
      "text": "O formulário do Showcase gera um rascunho Markdown local para copiar e anexar a uma contribuição. Não envia nada nem publica automaticamente. Relatos reais devem ter autorização do autor e informações verificáveis; nunca inclua chaves, tokens ou dados privados."
    },
    {
      "type": "h2",
      "text": "Escopo"
    },
    {
      "type": "para",
      "text": "Novas plataformas, eventos de apoio e outros provedores exigem implementação e testes; exemplos visuais não tornam essas integrações disponíveis."
    }
  ]
};

export default content;
