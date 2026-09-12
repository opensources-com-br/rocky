const content = {
  group: "Showcase",
  lead: "Quem transmite com o Rocky, o que mudou na live de cada um e como cada configuração foi montada. Os casos completos ficam na página de Showcase.",
  blocks: [
    {"type": "h2", "text": "Por tipo de live"},
    {"type": "para", "text": "O mesmo app é usado de formas bem diferentes. Aula ao vivo pede tom analítico e nenhuma interrupção; watch party de futebol pede o contrário. Vale ler o caso mais parecido com o seu antes de configurar o seu."},
    {"type": "table", "cols": "minmax(0,1fr) minmax(0,1.3fr) minmax(0,1.2fr)", "head": ["TIPO", "CONFIGURAÇÃO TÍPICA", "CASO"], "rows": [["Programação", "Local 8B, direto, sem interrupção", "ju.lia, marcos_dev"], ["Games", "Local 7B, 2 intervenções / 10min", "tati.plays, renan__"], ["Educação", "Remoto, contexto de live inteira", "Oficina Aberta, Bruno Silva"], ["Podcast", "Superchats automáticos acima de R$ 10", "Canal Sete"], ["Esportes", "Contexto de 2 min, interrupção ligada", "Mesa de Bar FC"], ["Arte", "Local 9B, conversa só por voz", "Ateliê da Nô"]]},
    {"type": "h2", "text": "O que mais mudou na prática"},
    {"type": "list", "items": ["Dúvida repetida sem resposta praticamente desapareceu — é o relato mais comum.", "O resumo pós-live substituiu a revisão da gravação para achar pendências e clipes.", "Quem tem as mãos ocupadas (marcenaria, ilustração, jogo) deixou de tocar no teclado durante a live.", "Superchat deixou de passar batido em transmissões com condução contínua."]},
    {"type": "h2", "text": "Trechos"},
    {"type": "cards", "items": [{"key": "ju.lia", "text": "“Parei de perder pergunta boa no meio de 40 mensagens por minuto.”"}, {"key": "renan__", "text": "“Rodo tudo offline. Nenhuma mensagem do meu chat sai da minha máquina.”"}, {"key": "Bruno Silva", "text": "“O resumo pós-live me economiza uma hora de edição por transmissão.”"}, {"key": "Ateliê da Nô", "text": "“Desenho de costas para o chat e ainda assim respondo todo mundo.”"}]},
    {"type": "h2", "text": "Enviar o seu"},
    {"type": "para", "text": "Se você transmite com o Rocky, abra um PR adicionando sua configuração ao diretório showcase: conte o que mudou na sua live, qual modelo e voz usa, e quantas plataformas mantém conectadas. A página de Contribuir tem o formato esperado."},
    {"type": "note", "text": "Os casos completos, com depoimento inteiro, métricas e o config.toml de cada um, estão na página de Showcase — link no topo do site."},
  ],
};

export default content;
