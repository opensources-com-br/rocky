const content = {
  "group": "Configuração",
  "lead": "A voz é opcional. Rocky usa vozes do sistema para responder e whisper.cpp local para transcrever o microfone.",
  "blocks": [
    {
      "type": "h2",
      "text": "Saída de voz"
    },
    {
      "type": "para",
      "text": "Em Voz, escolha uma voz instalada, ajuste a velocidade e teste a reprodução. macOS usa say; Windows usa System.Speech. No Mac, ajuste o volume pelo sistema. Piper, ElevenLabs, ducking e seleção de saída de áudio não estão implementados."
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
      "text": "Com voz preparada, o ouvinte inicia ao conectar a Twitch e pode ser alternado pelo rodapé. Diga “Rocky” seguido do pedido. O nome configurado do agente funciona como palavra de ativação. Ctrl + Shift + F8 captura um comando direto sem essa palavra."
    },
    {
      "type": "h2",
      "text": "Fim da fala"
    },
    {
      "type": "para",
      "text": "A captura termina após fala seguida de silêncio configurável entre 450 e 1500 ms, com máximo de oito segundos. É possível ajustar o limiar de ruído ou desativar essa detecção. A captura pausa durante transcrição e respostas."
    },
    {
      "type": "h2",
      "text": "Áudio e OBS"
    },
    {
      "type": "para",
      "text": "O WAV temporário é apagado ao terminar a transcrição, inclusive em erros tratados; um crash pode deixar temporários do sistema. Áudio bruto não é enviado à IA. Use fones e grave um teste: captura de áudio do desktop pode incluir Rocky na transmissão. Não existe monitor privado garantido."
    }
  ]
};

export default content;
