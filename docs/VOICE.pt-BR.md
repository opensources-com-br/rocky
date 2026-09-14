# Configuração de voz

Rocky oferece voz do sistema ou ElevenLabs para responder. O macOS usa `say`, e o Windows usa `System.Speech`. Em **Configurações → Voz**, escolha a voz, ajuste a velocidade e execute o teste de reprodução.

## ElevenLabs

Em **Configurações → Voz**, selecione **ElevenLabs**, cole a chave e clique em **Salvar chave**. A chave usa Keychain no macOS e DPAPI no Windows. Clique em **Carregar vozes e modelos** e escolha ambos, ou preencha seus IDs manualmente. O modelo inicial é `eleven_flash_v2_5`; sua conta precisa permitir a voz e o modelo selecionados.

Use o teste de reprodução antes da live. O texto da resposta é enviado à ElevenLabs e pode consumir créditos. A reprodução começa durante o recebimento do áudio. Falhas de chave, permissão ou quota aparecem no painel; a alternativa **Usar voz local** é opcional e só atua antes de começar a reprodução, evitando repetir uma resposta parcialmente falada. Cancelar interrompe o recebimento e a reprodução, mas não garante estorno de créditos.

## Reconhecimento do streamer

A voz é opcional. Depois de configurada, o ouvinte inicia ao conectar Twitch, Kick, YouTube, Facebook ou TikTok LIVE; o rodapé permite ligar e desligar. Rocky processa o áudio localmente em trechos curtos e aceita pedidos com seu nome, por atalho ou como continuação breve da resposta anterior. Diga, por exemplo, **“Rocky, o que o chat quer jogar?”**. O texto reconhecido aparece na aba Conversa, Rocky consulta até 200 mensagens recebidas nos últimos dois minutos e responde em áudio.

No macOS com Homebrew, clique em **Configurar reconhecimento de voz**. Rocky instala o `whisper.cpp`, baixa o modelo multilíngue e salva os caminhos no dispositivo. No Windows, selecione manualmente o executável `whisper-cli`, o modelo GGML e o microfone.

Use **Testar conversa por voz** antes da live. O painel mostra a frase transcrita e reproduz uma resposta para validar microfone, Whisper e saída de áudio juntos. Durante a sessão, o rodapé exibe o nível capturado; o topo diferencia `CHAT ATIVO` de `MICROFONE ON`.

O WAV temporário é mono, 16 bits e 16 kHz. Ele é apagado depois de cada transcrição, inclusive quando ocorre erro. O áudio bruto não é enviado ao provedor de IA nem mantido pelo Rocky.

Com detecção ativa, a fala seguida de silêncio encerra a captura; uma frase em andamento pode durar até 30 segundos. Sem detecção, os blocos continuam em oito segundos. Em Voz, ajuste o silêncio (450–1500 ms), calibre três segundos de ruído ambiente ou ajuste o limiar manualmente. Trechos sem fala são descartados. O microfone mantém um buffer durante a transcrição e escuta durante a análise; durante a reprodução ele pausa para evitar comandos vindos da própria voz. Use o atalho de fala para interromper uma resposta e reformular. Fones continuam recomendados; não há cancelamento acústico de eco.

Após uma resposta, “explica melhor”, “resume isso”, “repete” e comandos de salvamento dispensam o nome por 12 segundos. Chamar apenas “Rocky” abre oito segundos para o pedido seguinte. Uma nova pergunta direta cancela a análise anterior. “Rocky, para” cancela um pedido enquanto o ouvinte está ativo. O idioma da interface seleciona o idioma da resposta/transcrição; escolha uma voz compatível.

O download gerenciado usa revisão fixa e validação SHA-256. É possível cancelar e tentar novamente. Windows ainda exige whisper-cli/modelo externos; a preparação automática no macOS exige Homebrew. Com voz do sistema, o controle de volume fica desabilitado no Mac; ElevenLabs tem controle de volume no app.

Use fones e grave um teste no OBS: capturar áudio do desktop pode incluir o Rocky. Este candidato não seleciona um dispositivo de saída separado nem garante monitoramento privado.


## Atalhos e registro de ideias

Os atalhos globais padrão são Ctrl+Shift+F8 (capturar um comando diretamente, sem precisar dizer Rocky), Ctrl+Shift+F9 (silenciar/retomar) e Ctrl+Shift+F10 (mostrar/ocultar janela). No macOS, use Control, não Command; dependendo do teclado, pode ser necessário Fn. Altere os números F1–F12 em Voz. Um conflito de registro aparece no painel; escolha três teclas diferentes.

O atalho de fala inicia uma captura. O fim da fala ou o limite de captura envia o comando; não é necessário manter a tecla pressionada. O ouvinte permanece ativo depois da resposta e pode ser desligado no rodapé.

Com a palavra de ativação normal, diga:
- “Rocky, anota: fazer uma live sobre programação”: cria uma nota livre.
- “Rocky, ideia: jogar com o chat na sexta”: cria uma ideia livre.
- “Rocky, salva isso como nota” ou “Rocky, salva isso como ideias”: salva a resposta atual.

Salvar mantém a aba atual e mostra confirmação com Desfazer. Notas/Ideias também têm o botão Nova para digitação. O Histórico guarda até 30 respostas da sessão para consultar, repetir, copiar e salvar; as quatro últimas interações entram como contexto limitado de continuidade. Desconectar limpa esse histórico temporário, preservando notas/ideias salvas.

O botão Antes da live reúne a conexão da plataforma, teste de IA, teste de voz/microfone e orientação para conferir uma gravação no OBS. O áudio é opcional; a interface indica quais verificações foram feitas, sem presumir que a saída está isolada da transmissão.

Referência da integração: [API de streaming da ElevenLabs](https://elevenlabs.io/docs/api-reference/text-to-speech/stream).
