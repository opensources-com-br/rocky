# Configuração de voz

Rocky usa a voz instalada no computador para responder. O macOS usa `say`, e o Windows usa `System.Speech`. Em **Configurações → Voz**, escolha a voz, ajuste a velocidade e execute o teste de reprodução.

## Reconhecimento do streamer

A voz é opcional. Depois de configurada, o ouvinte inicia ao conectar a Twitch; o rodapé permite ligar e desligar. Rocky processa o áudio localmente em trechos curtos e só executa um comando quando reconhece seu nome. Diga, por exemplo, **“Rocky, o que o chat quer jogar?”**. O texto reconhecido aparece na aba Conversa, Rocky consulta até 200 mensagens recebidas nos últimos dois minutos e responde em áudio.

No macOS com Homebrew, clique em **Configurar reconhecimento de voz**. Rocky instala o `whisper.cpp`, baixa o modelo multilíngue e salva os caminhos no dispositivo. No Windows, selecione manualmente o executável `whisper-cli`, o modelo GGML e o microfone.

Use **Testar conversa por voz** antes da live. O painel mostra a frase transcrita e reproduz uma resposta para validar microfone, Whisper e saída de áudio juntos. Durante a sessão, o rodapé exibe o nível capturado; o topo diferencia `CHAT ATIVO` de `MICROFONE ON`.

O WAV temporário é mono, 16 bits e 16 kHz. Ele é apagado depois de cada transcrição, inclusive quando ocorre erro. O áudio bruto não é enviado ao provedor de IA nem mantido pelo Rocky.

Cada captura dura no máximo oito segundos e pode terminar antes, após detectar fala seguida de silêncio. Em Voz, ajuste o silêncio (450–1500 ms) e o limiar de ruído, ou desative a detecção para usar blocos fixos. Trechos sem fala detectada são descartados sem transcrição. Fale enquanto o medidor estiver ativo: a captura pausa na transcrição e nas respostas. Silenciar ou pular uma resposta retoma a captura quando o ouvinte está habilitado. O nome configurado do agente também é a palavra de ativação. O idioma da interface seleciona o idioma da resposta/transcrição; escolha uma voz instalada compatível.

O download gerenciado usa revisão fixa e validação SHA-256. É possível cancelar e tentar novamente. Windows ainda exige whisper-cli/modelo externos; a preparação automática no macOS exige Homebrew. O controle de volume fica desabilitado no Mac: use a saída do sistema.

Use fones e grave um teste no OBS: capturar áudio do desktop pode incluir o Rocky. Este candidato não seleciona um dispositivo de saída separado nem garante monitoramento privado.


## Atalhos e registro de ideias

Os atalhos globais padrão são Ctrl+Shift+F8 (capturar um comando diretamente, sem precisar dizer Rocky), Ctrl+Shift+F9 (silenciar/retomar) e Ctrl+Shift+F10 (mostrar/ocultar janela). No macOS, use Control, não Command; dependendo do teclado, pode ser necessário Fn. Altere os números F1–F12 em Voz. Um conflito de registro aparece no painel; escolha três teclas diferentes.

O atalho de fala inicia uma captura. O fim da fala ou o limite de oito segundos envia o comando; não é necessário manter a tecla pressionada. O ouvinte permanece ativo depois da resposta e pode ser desligado no rodapé.

Com a palavra de ativação normal, diga:
- “Rocky, anota: fazer uma live sobre programação”: cria uma nota livre.
- “Rocky, ideia: jogar com o chat na sexta”: cria uma ideia livre.
- “Rocky, salva isso como nota” ou “Rocky, salva isso como ideias”: salva a resposta atual.

Salvar mantém a aba atual e mostra confirmação com Desfazer. Notas/Ideias também têm o botão Nova para digitação. O Histórico guarda até 30 respostas da sessão para consultar, repetir, copiar e salvar; as quatro últimas interações entram como contexto limitado de continuidade. Desconectar limpa esse histórico temporário, preservando notas/ideias salvas.

O botão Antes da live reúne conexão Twitch, teste de IA, teste de voz/microfone e orientação para conferir uma gravação no OBS. O áudio é opcional; a interface indica quais verificações foram feitas, sem presumir que a saída está isolada da transmissão.
