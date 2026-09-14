# Dados e privacidade / Data handling

Rocky não exige conta própria nem coleta telemetria. Chat e transcrição ficam na memória durante o uso. A fila salva automaticamente perguntas detectadas, autores e trechos de evidência no SQLite; notas, ideias, momentos e resumos também persistem. Uma reconexão não recupera necessariamente mensagens perdidas. O app não grava a live inteira.

Chaves de IA/ElevenLabs, Client Secrets da Kick e do YouTube e o App Secret do Facebook são salvos no Keychain do macOS ou protegidos pelo DPAPI do usuário no Windows. Os arquivos `*-credential.dpapi` contêm somente conteúdo criptografado. Configurações não secretas usam preferências do sistema. Tokens da Twitch, Kick, YouTube e Facebook ficam na memória. Se o cofre falhar, o app não grava o segredo em texto simples. Chaves de IA legadas são migradas e removidas das preferências.

O conector do TikTok salva somente o nome de usuário nas preferências locais e não solicita senha, cookie ou token. Para localizar a sala e abrir o WebSocket, a biblioteca TikTokLiveJava comunica o nome de usuário e o identificador público da live ao TikTok e ao serviço Eulerstream. Essa conexão é não oficial e segue também as políticas de rede desses serviços.

Um modelo local no Ollama permite processamento de sugestões no computador; o endereço loopback sozinho não comprova execução local. OpenAI, Anthropic e OpenRouter recebem o pedido e a amostra de chat; OpenRouter também encaminha ao provedor do modelo. Retenção, uso e faturamento desses serviços seguem a conta e os controles do usuário. `store=false` na OpenAI não representa uma garantia geral de retenção zero.

Ao selecionar ElevenLabs, o texto preparado para fala (inclusive testes e trechos de chat presentes na resposta) é enviado ao serviço. Áudio de microfone continua sendo transcrito localmente; a chave não acompanha pedidos à IA. Síntese segue a política e o faturamento da conta ElevenLabs. As durações de captura, transcrição e reprodução são medidas apenas no dispositivo, sem envio de métricas.

Áudio temporário é removido ao terminar a transcrição, inclusive em erro tratado. Um encerramento abrupto do processo/sistema pode deixar temporários no diretório temporário do SO; não há promessa de apagamento seguro após crash. O modelo de transcrição gerenciado é baixado do Hugging Face; Homebrew pode ser usado para instalar whisper.cpp no Mac. Essas preparações exigem rede, mesmo quando a análise futura é local.

Em **Configurações → Dados** é possível abrir a pasta, exportar/apagar notas, redefinir configurações e remover o modelo gerenciado. Redefinir configurações remove as credenciais salvas e encerra a sessão; reabra o app para aplicar todos os padrões. Modelos selecionados fora da pasta gerenciada e exportações nunca são apagados por essas ações. A exclusão é lógica: não apaga backups, dados retidos por provedores ou cópias exportadas. SQLite não é criptografado pelo Rocky.

Localizações: macOS `~/Library/Application Support/Rocky`; Windows `%APPDATA%/Rocky`. Preferências ficam sob o namespace `dev/rocky` nas preferências do sistema. Para backup consistente do banco, feche o Rocky antes de copiar a pasta. Não publique chaves, arquivos do cofre, banco ou transcrições em issues.

## English

Rocky has no account system or telemetry. Chat/transcripts are temporary in-memory data. Detected questions and author evidence are automatically saved in local SQLite, alongside notes, ideas, moments and session summaries. Saved AI/ElevenLabs keys, Kick and YouTube Client Secrets, and the Facebook App Secret use macOS Keychain or user-scoped Windows DPAPI, never plaintext preferences. Twitch, Kick, YouTube and Facebook tokens are discarded when the app closes. A failed secure-storage operation does not fall back to plaintext.

The TikTok connector stores only the username in local preferences and does not request a password, cookie, or token. TikTokLiveJava sends the username and public live-room identifier to TikTok and Eulerstream to locate the room and open its WebSocket. This is an unofficial connection and is also subject to those services' network policies.

With ElevenLabs selected, prepared speech text, including test phrases and chat excerpts in an answer, is sent to that service under its account policies. Microphone transcription stays local. Audio timing measurements stay on the device.

Cloud providers, including OpenAI, Anthropic and OpenRouter, receive selected chat and the request; their account policies apply. A local Ollama model can process suggestions on the computer; a loopback address alone does not prove local inference. Model downloads and Homebrew setup require network access. Temporary transcription files are deleted after normal/error completion; an abrupt crash can leave OS temporary files. SQLite itself is not encrypted by Rocky.

**Settings → Data** provides export, note deletion, configuration/key reset, managed-model removal and folder access. Deletion is logical; exports, external models, backups and provider-held data are not erased. Close Rocky before copying the data folder for backup. Never attach keys, credential files, notes databases or transcripts to public issues.

## Backup, diagnóstico e atualização

O backup JSON em **Dados** inclui registros, fontes, identificação da live e estado de conclusão, sem credenciais. Pode ser criado com o app aberto. A importação exige confirmação, ignora cópias idênticas e cancela diante de um ID conflitante; não substitui registros existentes. Limites: 50 MB e 10.000 registros por arquivo.

O diagnóstico tem prévia e exportação local: versão, estados de conexão/áudio/IA e contagens. Não contém mensagens, chaves, nomes de canal ou caminhos; não é enviado automaticamente.

A consulta de atualização, desativável em **Dados**, acessa a API pública do GitHub na inicialização. O GitHub recebe os dados normais da conexão de rede. O download solicitado pelo usuário acessa o GitHub e seu armazenamento de releases, verifica o SHA-256 publicado e salva o instalador na subpasta `updates` dos dados locais. Não envia registros nem credenciais. Arquivos parciais são removidos ao falhar ou cancelar; instaladores completos permanecem para nova tentativa. A instalação e o reinício são manuais.

Backups contain saved records and evidence, but no credentials. Import is confirmed and preserves existing records. Diagnostics are previewed/exported locally without message contents or secrets. The optional startup update check contacts GitHub's public API. User-requested downloads contact GitHub release storage and verify published SHA-256 checksums. Installers remain in the local `updates` directory; failed or cancelled partial downloads are removed. Installation and restart are manual.
