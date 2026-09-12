# Dados e privacidade / Data handling

Rocky não exige conta própria nem coleta telemetria. Chat e transcrição ficam na memória durante o uso. A fila salva automaticamente perguntas detectadas, autores e trechos de evidência no SQLite; notas, ideias, momentos e resumos também persistem. Uma reconexão não recupera necessariamente mensagens perdidas. O app não grava a live inteira.

Chaves de IA e o Client Secret da Kick são salvos no Keychain do macOS ou protegidos pelo DPAPI do usuário no Windows. Os arquivos `*-credential.dpapi` contêm somente conteúdo criptografado. Configurações não secretas usam preferências do sistema. Tokens da Twitch e da Kick ficam na memória. Se o cofre falhar, o app não grava o segredo em texto simples. Chaves de IA legadas são migradas e removidas das preferências.

Ollama em loopback permite processamento de sugestões no computador. OpenAI/OpenRouter recebem o pedido e a amostra de chat; OpenRouter também encaminha ao provedor do modelo. Retenção, uso e faturamento desses serviços seguem a conta e os controles do usuário. `store=false` na OpenAI não representa uma garantia geral de retenção zero.

Áudio temporário é removido ao terminar a transcrição, inclusive em erro tratado. Um encerramento abrupto do processo/sistema pode deixar temporários no diretório temporário do SO; não há promessa de apagamento seguro após crash. O modelo de transcrição gerenciado é baixado do Hugging Face; Homebrew pode ser usado para instalar whisper.cpp no Mac. Essas preparações exigem rede, mesmo quando a análise futura é local.

Em **Configurações → Dados** é possível abrir a pasta, exportar/apagar notas, redefinir configurações e remover o modelo gerenciado. Redefinir configurações remove a chave salva e encerra a sessão; reabra o app para aplicar todos os padrões. Modelos selecionados fora da pasta gerenciada e exportações nunca são apagados por essas ações. A exclusão é lógica: não apaga backups, dados retidos por provedores ou cópias exportadas. SQLite não é criptografado pelo Rocky.

Localizações: macOS `~/Library/Application Support/Rocky`; Windows `%APPDATA%/Rocky`. Preferências ficam sob o namespace `dev/rocky` nas preferências do sistema. Para backup consistente do banco, feche o Rocky antes de copiar a pasta. Não publique chaves, arquivos do cofre, banco ou transcrições em issues.

## English

Rocky has no account system or telemetry. Chat/transcripts are temporary in-memory data. Detected questions and author evidence are automatically saved in local SQLite, alongside notes, ideas, moments and session summaries. Saved AI keys and the Kick Client Secret use macOS Keychain or user-scoped Windows DPAPI, never plaintext preferences. Twitch and Kick tokens are discarded when the app closes. A failed secure-storage operation does not fall back to plaintext.

Cloud providers receive selected chat and the request; their account policies apply. Local loopback Ollama can process suggestions on the computer. Model downloads and Homebrew setup require network access. Temporary transcription files are deleted after normal/error completion; an abrupt crash can leave OS temporary files. SQLite itself is not encrypted by Rocky.

**Settings → Data** provides export, note deletion, configuration/key reset, managed-model removal and folder access. Deletion is logical; exports, external models, backups and provider-held data are not erased. Close Rocky before copying the data folder for backup. Never attach keys, credential files, notes databases or transcripts to public issues.

## Backup, diagnóstico e atualização

O backup JSON em **Dados** inclui registros, fontes, identificação da live e estado de conclusão, sem credenciais. Pode ser criado com o app aberto. A importação exige confirmação, ignora cópias idênticas e cancela diante de um ID conflitante; não substitui registros existentes. Limites: 50 MB e 10.000 registros por arquivo.

O diagnóstico tem prévia e exportação local: versão, estados de conexão/áudio/IA e contagens. Não contém mensagens, chaves, nomes de canal ou caminhos; não é enviado automaticamente.

A consulta de atualização, desativável em **Dados**, acessa a API pública do GitHub na inicialização. O GitHub recebe os dados normais da conexão de rede. O Rocky compara versões das releases oficiais e oferece um link; não instala atualizações. Não envia registros nem credenciais nessa consulta.

Backups contain saved records and evidence, but no credentials. Import is confirmed and preserves existing records. Diagnostics are previewed/exported locally without message contents or secrets. The optional startup update check contacts GitHub's public API; updates are opened through an official release link, never installed automatically.
