# Configuração de voz

Rocky pode ler novas sugestões usando uma voz instalada no computador. A leitura começa desativada em instalações novas; preferências já salvas são preservadas. A síntese acontece no próprio dispositivo: o macOS usa `say`, e o Windows usa `System.Speech`. Abra **Configurações → Voz** para ativar a leitura, escolher uma voz, ajustar a velocidade ou testar a reprodução.

Rocky usa a saída de áudio padrão do sistema. No Windows, também aplica o volume selecionado no Rocky. No macOS, use o volume de saída do sistema. Uma fonte que capture todo o áudio do computador no OBS pode incluir a voz do Rocky; faça uma gravação curta de teste antes da live.

## Transcrição local

A entrada do streamer funciona por push-to-talk e usa o [`whisper.cpp`](https://github.com/ggml-org/whisper.cpp) localmente. Rocky ainda não baixa nem inclui um modelo no instalador. Instale o `whisper.cpp` e baixe um modelo GGML multilíngue, como o `base`:

```shell
git clone https://github.com/ggml-org/whisper.cpp.git
cd whisper.cpp
sh ./models/download-ggml-model.sh base
cmake -B build
cmake --build build -j --config Release
```

No Windows, execute o script de download do modelo pelo Git Bash e faça o build em um ambiente CMake que gere o executável Release.

Depois, abra **Configurações → Voz** e selecione:

- o executável `whisper-cli` (`build/bin/whisper-cli` no macOS e, normalmente, `build/bin/Release/whisper-cli.exe` no Windows);
- o modelo baixado, por exemplo `models/ggml-base.bin`;
- o microfone, ou mantenha **Entrada padrão**.

Durante uma sessão real da Twitch, clique em **Fale com o Rocky**, fale e clique novamente. Rocky grava no máximo 60 segundos, transcreve o comando, mostra o texto na aba Conversa e pede ao modelo de linguagem configurado uma resposta baseada nas mensagens recebidas do chat. A resposta gerada continua obrigada a citar IDs reais dessas mensagens.

A captura do microfone só começa depois do clique. O arquivo WAV temporário, mono, 16 bits e 16 kHz, é apagado depois da transcrição, mesmo em caso de erro. A escuta contínua e a retenção do áudio bruto não estão ativadas.
