const content = {
  "group": "Configuração",
  "lead": "A voz é opcional. Rocky usa voz do sistema ou ElevenLabs para responder e whisper.cpp local para transcrever o microfone.",
  "blocks": [
    {
      "type": "h2",
      "text": "Saída de voz"
    },
    {
      "type": "para",
      "text": "Em Voz, escolha o provedor e teste a reprodução. A voz do sistema usa say no macOS e System.Speech no Windows. No Mac, essa opção usa o volume do sistema. ElevenLabs permite volume no app. Piper, ducking e seleção de saída não estão implementados."
    },
    {
      "type": "h2",
      "text": "ElevenLabs"
    },
    {
      "type": "para",
      "text": "Selecione ElevenLabs, salve sua chave e clique em Carregar vozes e modelos. Escolha ambos ou informe os IDs manualmente; o modelo inicial é eleven_flash_v2_5. A chave usa o cofre do sistema. Testes e respostas enviam texto à ElevenLabs e podem consumir créditos. O áudio toca enquanto chega; falhas de chave, permissão e quota aparecem no painel. A alternativa local é opcional e só atua antes de começar a reprodução."
    },
    {
      "type": "h2",
      "text": "Reconhecimento"
    },
    {
      "type": "para",
      "text": "No macOS com Homebrew, Configurar reconhecimento de voz instala whisper.cpp e baixa o modelo gerenciado. No Windows, selecione manualmente o executável whisper-cli, o modelo GGML e o microfone. A preparação precisa de rede; a transcrição é local."
    },
    {
      "type": "h2",
      "text": "Como falar"
    },
    {
      "type": "para",
      "text": "Com voz preparada, o ouvinte inicia ao conectar Twitch, Kick, YouTube, Facebook ou TikTok LIVE e pode ser alternado pelo rodapé. Diga “Rocky” seguido do pedido. O nome configurado do agente funciona como palavra de ativação. Ctrl + Shift + F8 interrompe a fala e captura um comando direto sem essa palavra. Após uma resposta, continuações como “explica melhor”, “resume isso” e “salva isso como nota” dispensam o nome por 12 segundos. Uma nova pergunta substitui a análise anterior."
    },
    {
      "type": "h2",
      "text": "Fim da fala"
    },
    {
      "type": "para",
      "text": "A captura termina após fala seguida de silêncio entre 450 e 1500 ms. Frases em andamento podem durar até 30 segundos; sem detecção, são blocos de oito segundos. Calibre o ruído em Voz ou ajuste o limiar. O microfone mantém um buffer durante transcrição e escuta durante análise; pausa na reprodução para evitar reconhecer a própria voz. Use o atalho de fala para interromper e reformular."
    },
    {
      "type": "h2",
      "text": "Áudio e OBS"
    },
    {
      "type": "para",
      "text": "O WAV temporário é apagado ao terminar a transcrição, inclusive em erros tratados; um crash pode deixar temporários do sistema. Áudio bruto não é enviado à IA. Use fones e grave um teste com as mesmas fontes do OBS da live: fones sozinhos não impedem que a captura de áudio inclua Rocky. Não existe monitor privado garantido."
    }
  ]
};

export default content;
