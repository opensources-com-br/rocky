# Dados e privacidade / Data handling

Rocky não exige conta própria nem coleta telemetria. Chat e transcrição permanecem na memória durante o uso; notas escolhidas e trechos de evidência persistem no SQLite. Uma reconexão não recupera necessariamente mensagens perdidas. O app não grava a live inteira.

Chaves de IA são salvas explicitamente no Keychain do macOS ou protegidas pelo DPAPI do usuário no Windows. O arquivo `ai-credential.dpapi` contém somente o conteúdo criptografado. Configurações não secretas usam preferências do sistema. Tokens Twitch ficam na memória. Edições de chave não salvas são descartadas ao fechar. Se o cofre falhar, o app mostra um aviso e não grava a chave em texto simples. Chaves legadas são migradas e removidas das preferências.

Ollama em loopback permite processamento de sugestões no computador. OpenAI/OpenRouter recebem o pedido e a amostra de chat; OpenRouter também encaminha ao provedor do modelo. Retenção, uso e faturamento desses serviços seguem a conta e os controles do usuário. `store=false` na OpenAI não representa uma garantia geral de retenção zero.

Áudio temporário é removido ao terminar a transcrição, inclusive em erro tratado. Um encerramento abrupto do processo/sistema pode deixar temporários no diretório temporário do SO; não há promessa de apagamento seguro após crash. O modelo de transcrição gerenciado é baixado do Hugging Face; Homebrew pode ser usado para instalar whisper.cpp no Mac. Essas preparações exigem rede, mesmo quando a análise futura é local.

Em **Configurações → Dados** é possível abrir a pasta, exportar/apagar notas, redefinir configurações e remover o modelo gerenciado. Redefinir configurações remove a chave salva e encerra a sessão; reabra o app para aplicar todos os padrões. Modelos selecionados fora da pasta gerenciada e exportações nunca são apagados por essas ações. A exclusão é lógica: não apaga backups, dados retidos por provedores ou cópias exportadas. SQLite não é criptografado pelo Rocky.

Localizações: macOS `~/Library/Application Support/Rocky`; Windows `%APPDATA%/Rocky`. Preferências ficam sob o namespace `dev/rocky` nas preferências do sistema. Para backup consistente do banco, feche o Rocky antes de copiar a pasta. Não publique chaves, arquivos do cofre, banco ou transcrições em issues.

## English

Rocky has no account system or telemetry. Chat/transcripts are temporary in-memory data; selected notes and evidence persist in local SQLite. Saved AI keys use macOS Keychain or user-scoped Windows DPAPI, never plaintext preferences. Unsaved key edits and Twitch tokens are discarded when the app closes. A failed secure-storage operation is reported instead of silently storing plaintext.

Cloud providers receive selected chat and the request; their account policies apply. Local loopback Ollama can process suggestions on the computer. Model downloads and Homebrew setup require network access. Temporary transcription files are deleted after normal/error completion; an abrupt crash can leave OS temporary files. SQLite itself is not encrypted by Rocky.

**Settings → Data** provides export, note deletion, configuration/key reset, managed-model removal and folder access. Deletion is logical; exports, external models, backups and provider-held data are not erased. Close Rocky before copying the data folder for backup. Never attach keys, credential files, notes databases or transcripts to public issues.
