# Configuração de voz

Rocky usa a voz instalada no computador para responder. O macOS usa `say`, e o Windows usa `System.Speech`. Em **Configurações → Voz**, escolha a voz, ajuste a velocidade e execute o teste de reprodução.

## Reconhecimento do streamer

A voz é opcional. Depois de configurada, o ouvinte inicia ao conectar a Twitch; o rodapé permite ligar e desligar. Rocky processa o áudio localmente em trechos curtos e só executa um comando quando reconhece seu nome. Diga, por exemplo, **“Rocky, o que o chat quer jogar?”**. O texto reconhecido aparece na aba Conversa, Rocky consulta até 200 mensagens recebidas nos últimos dois minutos e responde em áudio.

No macOS com Homebrew, clique em **Configurar reconhecimento de voz**. Rocky instala o `whisper.cpp`, baixa o modelo multilíngue e salva os caminhos no dispositivo. No Windows, selecione manualmente o executável `whisper-cli`, o modelo GGML e o microfone.

Use **Testar conversa por voz** antes da live. O painel mostra a frase transcrita e reproduz uma resposta para validar microfone, Whisper e saída de áudio juntos. Durante a sessão, o rodapé exibe o nível capturado; o topo diferencia `CHAT ATIVO` de `MICROFONE ON`.

O WAV temporário é mono, 16 bits e 16 kHz. Ele é apagado depois de cada transcrição, inclusive quando ocorre erro. O áudio bruto não é enviado ao provedor de IA nem mantido pelo Rocky.

Cada captura dura oito segundos. Fale enquanto o medidor estiver ativo: a captura pausa na transcrição e nas respostas. Silenciar ou pular uma resposta retoma a captura quando o ouvinte está habilitado. O nome configurado do agente também é a palavra de ativação. O idioma da interface seleciona o idioma da resposta/transcrição; escolha uma voz instalada compatível.

O download gerenciado usa revisão fixa e validação SHA-256. É possível cancelar e tentar novamente. Windows ainda exige whisper-cli/modelo externos; a preparação automática no macOS exige Homebrew. O controle de volume fica desabilitado no Mac: use a saída do sistema.

Use fones e grave um teste no OBS: capturar áudio do desktop pode incluir o Rocky. Este candidato não seleciona um dispositivo de saída separado nem garante monitoramento privado.
