const content = {
  group: "Começando",
  lead: "Rocky é um assistente de live que roda na sua máquina. Ele lê o chat de todas as suas plataformas ao mesmo tempo, separa o que merece resposta, lê superchats em voz alta e conversa com você por voz enquanto a transmissão acontece.",
  blocks: [
    {"type": "h2", "text": "O problema"},
    {"type": "para", "text": "Quando o chat passa de trinta mensagens por minuto, ler tudo deixa de ser possível. A pergunta boa se perde entre repetições, emotes e conversa paralela — e quem perguntou vai embora sem resposta."},
    {"type": "para", "text": "Rocky fica entre você e esse volume. Ele acompanha o chat inteiro, reconhece o que se repete, o que ficou sem resposta e o que merece atenção agora, e fala isso no seu ouvido."},
    {"type": "h2", "text": "Como ele se encaixa na sua transmissão"},
    {"type": "steps", "items": [{"n": "01", "title": "Uma janela flutuante", "text": "Rocky roda por cima do seu software de transmissão, sempre visível para você e nunca capturado na cena."}, {"n": "02", "title": "Uma fila de chat só", "text": "Twitch, Kick, YouTube e Facebook entram na mesma fila, com a origem de cada mensagem preservada."}, {"n": "03", "title": "Voz nos dois sentidos", "text": "Você segura uma tecla e pergunta; ele responde no seu monitor de áudio. Nada disso vai para o ar."}]},
    {"type": "h2", "text": "O que ele não é"},
    {"type": "list", "items": ["Não é um bot de chat: Rocky fala com você, não com o público, a menos que você peça para ele enviar algo.", "Não é um serviço: não existe conta Rocky, servidor Rocky nem assinatura. O app roda local.", "Não é um moderador: ele não bane, não silencia e não aplica regras. Ele informa e você decide."]},
    {"type": "note", "text": "Rocky é software livre sob licença MIT. Você pode ler, modificar e redistribuir o código."},
  ],
};

export default content;
