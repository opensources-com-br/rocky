# Preparação do candidato / Release preparation

Candidato: **1.0.11-alpha.1**, versão nativa **1.0.11**. Escopo atual: Twitch, Kick, YouTube, Facebook, TikTok LIVE, IA, voz do sistema ou ElevenLabs, perguntas agrupadas, notas, ideias, momentos, exportação e resumo dos registros locais ao desconectar ou fechar o app. Kick, YouTube e Facebook exigem credenciais próprias; a Kick também exige webhook HTTPS público e o Facebook exige uma Página com live ativa. O TikTok usa WebCast não oficial via Eulerstream e exige uma live pública ativa. Super Chats, Estrelas, presentes e geração automática de ideias ficam fora deste candidato. Não promover a estável antes do protocolo real.

## Build e identidade

Use JDK 17. Execute `./gradlew build :apps:desktop:packageDmg` no macOS; no Windows, `./gradlew.bat build :apps:desktop:packageMsi :apps:desktop:packageExe`.

Inclua o Client ID público por `-ProckyTwitchClientId=...` quando gerar o pacote oficial. A variável do repositório é usada no workflow de release; validar o cadastro OAuth e a autorização no instalador continua obrigatório. Não distribua Client Secrets da Kick ou do YouTube nem chaves de IA ou ElevenLabs.

Depois do empacotamento, execute `python3 scripts/release_metadata.py` (Windows: `python`). O script identifica versão, commit e arquitetura nos nomes e em `BUILDINFO-*.json`. Gere checksums **depois** da assinatura/notarização. Não sobrescreva candidatos já nomeados: arquive o pacote anterior e gere outro. O tag deve corresponder exatamente a `rockyVersion`; cada novo candidato deve ter versão nativa adequada à sequência de upgrades.

A matriz atual gera um pacote por runner macOS/Windows. Consulte a arquitetura gravada no manifesto; isso não comprova suporte a outras arquiteturas, em particular macOS Intel. O app mostra a identidade em **Configurações → Dados** e a exportação inclui versão/commit.

## Assinatura e notarização

Alpha acompanhada pode continuar explicitamente sem assinatura. Para entrega ampla, a pessoa responsável pela publicação precisa fornecer certificados e contas próprios; não há segredos de assinatura no repositório.

No Mac, instale a identidade Developer ID no Keychain. Defina `ROCKY_MAC_SIGN=true`, `ROCKY_MAC_SIGN_IDENTITY` e, se necessário, `ROCKY_MAC_KEYCHAIN` antes do build. Cadastre suas credenciais com `xcrun notarytool store-credentials`, defina `ROCKY_NOTARY_PROFILE` e execute `scripts/notarize-macos.sh`. O script verifica a assinatura, exige Developer ID, envia um único DMG, anexa e valida o ticket. Essa operação depende dos serviços Apple e não foi executada sem suas credenciais.

O workflow de release exige os secrets `ROCKY_MAC_CERTIFICATE_BASE64`, `ROCKY_MAC_CERTIFICATE_PASSWORD`, `ROCKY_MAC_SIGN_IDENTITY`, `ROCKY_APPLE_ID`, `ROCKY_APPLE_TEAM_ID` e `ROCKY_APPLE_APP_PASSWORD`. Exporte o certificado **Developer ID Application** com a chave privada como PKCS#12 (`.p12`) e grave sua representação Base64 em `ROCKY_MAC_CERTIFICATE_BASE64`. Use em `ROCKY_MAC_SIGN_IDENTITY` o nome completo mostrado por `security find-identity -v -p codesigning`. `ROCKY_APPLE_APP_PASSWORD` deve ser uma senha específica de app, não a senha principal da conta. O keychain usado pelo runner é temporário e removido ao final do job.

Releases anteriores não são alteradas por essa automação. Depois de cadastrar os secrets, publique uma nova tag e confirme `codesign`, `spctl`, `stapler validate` e a abertura em uma instalação limpa antes de apontar usuários para o novo DMG.

No Windows, instale o certificado de assinatura e sua chave privada no repositório do usuário. Configure `ROCKY_WINDOWS_CERT_THUMBPRINT` e disponibilize `signtool.exe` (ou `ROCKY_SIGNTOOL`). Execute `scripts/sign-windows.ps1` após gerar um MSI e um EXE; o script assina os instaladores com timestamp e verifica o resultado. O executável instalado e os avisos do sistema também precisam de validação no Windows; assinatura dos instaladores não substitui essa etapa.

O workflow público atual permanece no canal prerelease e não presume certificados instalados em runners hospedados. Configure a importação dos certificados no ambiente de publicação antes de automatizar assinatura ali. A promoção para estável é uma ação deliberada do responsável, depois de verificar os pacotes assinados e o protocolo; não é consequência de um tag ou de testes unitários verdes.

## Atualização e recuperação

Em Configurações → Dados, **Baixar atualização** seleciona DMG/MSI por sistema e arquitetura, mostra progresso e valida tamanho e SHA-256. Downloads incompletos, corrompidos ou cancelados são descartados. A abertura revalida o arquivo e fica bloqueada com plataformas ativas. Feche Rocky antes de concluir o instalador; no macOS, substitua o app em Aplicativos. O pacote verificado permanece na subpasta `updates` para nova tentativa. Não há substituição silenciosa, reinício automático ou rollback automático nesta versão.

Validação obrigatória entre dois instaladores: download e cancelamento; nova tentativa offline/online; pacote alterado; tentativa com conexão ativa; cancelamento do instalador; upgrade com preservação de notas, fontes, preferências e credenciais; reabertura na versão esperada. Execute em macOS e Windows. Testes automatizados não homologam a instalação nativa nem a assinatura dos releases.

Feche o Rocky e copie a pasta de dados antes de validar um upgrade. Instale o candidato sobre a versão anterior; confira reabertura, notas/fontes, versão nativa e credenciais migradas. Não abra um banco migrado com uma versão antiga: restaure a cópia completa se precisar retornar. O app não instala atualizações automaticamente durante uma live.

Se o banco não abrir, o app preserva o arquivo e permite tentar carregar novamente. Use Dados para localizar a pasta e faça uma cópia antes de reparos. Não apague o banco como primeiro passo. Exportações e backups não são apagados pela limpeza do app.

## Liberação

Execute [o roteiro do candidato](TEST_TODAY.pt-BR.md) e [o protocolo com streamers](STREAMER_TEST.pt-BR.md). Instalação limpa e upgrade em macOS/Windows, cofre nativo, microfone, OAuth real da Twitch, Kick, YouTube e Facebook, webhook Kick público, Página e live reais do Facebook, live pública e conexão WebCast real do TikTok, modelos de IA reais, ElevenLabs com chave/voz reais e cancelamento, OBS e lives prolongadas continuam sendo verificações humanas. Nenhum resultado deve ser preenchido como aprovado por inferência do build.

## English

This candidate targets Twitch, Kick, YouTube, Facebook, TikTok LIVE, AI, system/ElevenLabs speech, grouped questions, notes, ideas, moments and a local records summary on disconnect or app close. Kick, YouTube and Facebook require developer credentials; Kick also requires public HTTPS webhook forwarding and Facebook requires an active live on an authorized Page. TikTok requires an active public live and uses unofficial WebCast through Eulerstream. Build with JDK 17; run `scripts/release_metadata.py` after packaging/signing to record version, commit and architecture. The release tag must match `rockyVersion`.

macOS signing uses an installed Developer ID identity via `ROCKY_MAC_SIGN=true` and `ROCKY_MAC_SIGN_IDENTITY`; notarization uses a `notarytool` profile through `scripts/notarize-macos.sh`. Windows installer signing uses an installed certificate selected by `ROCKY_WINDOWS_CERT_THUMBPRINT` through `scripts/sign-windows.ps1`. These require publisher-owned credentials and validation on the target OS. Hosted CI does not automatically import certificates.

Back up the data directory with Rocky closed before upgrading. Verify notes, evidence, native version and credential migration after installation. Clean installation, upgrade, real Twitch, Kick, YouTube and Facebook OAuth, Kick webhooks, Facebook Page live comments, a real TikTok LIVE WebCast session, AI, system/ElevenLabs speech with real credentials and cancellation, system credential storage, OBS and long streams remain manual release gates.
