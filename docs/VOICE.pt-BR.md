# Configuração de voz

Rocky usa a voz instalada no computador para responder. O macOS usa `say`, e o Windows usa `System.Speech`. Em **Configurações → Voz**, escolha a voz, ajuste a velocidade e execute o teste de reprodução.

## Reconhecimento do streamer

O microfone permanece ativo durante a sessão real. Rocky processa o áudio localmente em trechos curtos e só executa um comando quando reconhece seu nome. Diga, por exemplo, **“Rocky, o que o chat quer jogar?”**. O texto reconhecido aparece na aba Conversa, Rocky consulta as mensagens dos últimos dois minutos e responde em áudio.

No macOS com Homebrew, clique em **Configurar reconhecimento de voz**. Rocky instala o `whisper.cpp`, baixa o modelo multilíngue e salva os caminhos no dispositivo. No Windows, selecione manualmente o executável `whisper-cli`, o modelo GGML e o microfone.

Use **Testar conversa por voz** antes da live. O painel mostra a frase transcrita e reproduz uma resposta para validar microfone, Whisper e saída de áudio juntos. Durante a sessão, o rodapé exibe o nível capturado; o topo diferencia `CHAT ATIVO` de `MICROFONE ON`.

O WAV temporário é mono, 16 bits e 16 kHz. Ele é apagado depois de cada transcrição, inclusive quando ocorre erro. O áudio bruto não é enviado ao provedor de IA nem mantido pelo Rocky.
