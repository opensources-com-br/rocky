# Preparação do candidato / Release preparation

Candidato: **1.0.11-alpha.1**, versão nativa **1.0.11**. Escopo atual: Twitch, Kick, YouTube, IA, voz opcional, perguntas agrupadas, notas, ideias, momentos, exportação e resumo dos registros locais ao desconectar ou fechar o app. Kick e YouTube exigem credenciais próprias; a Kick também exige webhook HTTPS público. Super Chats e geração automática de ideias ficam fora deste candidato. Não promover a estável antes do protocolo real.

## Build e identidade

Use JDK 17. Execute `./gradlew build :apps:desktop:packageDmg` no macOS; no Windows, `./gradlew.bat build :apps:desktop:packageMsi :apps:desktop:packageExe`.

Inclua o Client ID público por `-ProckyTwitchClientId=...` quando gerar o pacote oficial. A variável do repositório é usada no workflow de release; validar o cadastro OAuth e a autorização no instalador continua obrigatório. Não distribua Client Secrets da Kick ou do YouTube nem chaves privadas de IA.

Depois do empacotamento, execute `python3 scripts/release_metadata.py` (Windows: `python`). O script identifica versão, commit e arquitetura nos nomes e em `BUILDINFO-*.json`. Gere checksums **depois** da assinatura/notarização. Não sobrescreva candidatos já nomeados: arquive o pacote anterior e gere outro. O tag deve corresponder exatamente a `rockyVersion`; cada novo candidato deve ter versão nativa adequada à sequência de upgrades.

A matriz atual gera um pacote por runner macOS/Windows. Consulte a arquitetura gravada no manifesto; isso não comprova suporte a outras arquiteturas, em particular macOS Intel. O app mostra a identidade em **Configurações → Dados** e a exportação inclui versão/commit.

## Assinatura e notarização

Alpha acompanhada pode continuar explicitamente sem assinatura. Para entrega ampla, a pessoa responsável pela publicação precisa fornecer certificados e contas próprios; não há segredos de assinatura no repositório.

No Mac, instale a identidade Developer ID no Keychain. Defina `ROCKY_MAC_SIGN=true`, `ROCKY_MAC_SIGN_IDENTITY` e, se necessário, `ROCKY_MAC_KEYCHAIN` antes do build. Cadastre suas credenciais com `xcrun notarytool store-credentials`, defina `ROCKY_NOTARY_PROFILE` e execute `scripts/notarize-macos.sh`. O script verifica a assinatura, exige Developer ID, envia um único DMG, anexa e valida o ticket. Essa operação depende dos serviços Apple e não foi executada sem suas credenciais.

No Windows, instale o certificado de assinatura e sua chave privada no repositório do usuário. Configure `ROCKY_WINDOWS_CERT_THUMBPRINT` e disponibilize `signtool.exe` (ou `ROCKY_SIGNTOOL`). Execute `scripts/sign-windows.ps1` após gerar um MSI e um EXE; o script assina os instaladores com timestamp e verifica o resultado. O executável instalado e os avisos do sistema também precisam de validação no Windows; assinatura dos instaladores não substitui essa etapa.

O workflow público atual permanece no canal prerelease e não presume certificados instalados em runners hospedados. Configure a importação dos certificados no ambiente de publicação antes de automatizar assinatura ali. A promoção para estável é uma ação deliberada do responsável, depois de verificar os pacotes assinados e o protocolo; não é consequência de um tag ou de testes unitários verdes.

## Atualização e recuperação

Feche o Rocky e copie a pasta de dados antes de validar um upgrade. Instale o candidato sobre a versão anterior; confira reabertura, notas/fontes, versão nativa e credenciais migradas. Não abra um banco migrado com uma versão antiga: restaure a cópia completa se precisar retornar. O app não instala atualizações automaticamente durante uma live.

Se o banco não abrir, o app preserva o arquivo e permite tentar carregar novamente. Use Dados para localizar a pasta e faça uma cópia antes de reparos. Não apague o banco como primeiro passo. Exportações e backups não são apagados pela limpeza do app.

## Liberação

Execute [o roteiro do candidato](TEST_TODAY.pt-BR.md) e [o protocolo com streamers](STREAMER_TEST.pt-BR.md). Instalação limpa e upgrade em macOS/Windows, cofre nativo, microfone, OAuth real da Twitch, Kick e YouTube, webhook Kick público, modelos de IA reais, OBS e lives prolongadas continuam sendo verificações humanas. Nenhum resultado deve ser preenchido como aprovado por inferência do build.

## English

This candidate targets Twitch, Kick, YouTube, AI, optional voice, grouped questions, notes, ideas, moments and a local records summary on disconnect or app close. Kick and YouTube require developer credentials; Kick also requires public HTTPS webhook forwarding. Build with JDK 17; run `scripts/release_metadata.py` after packaging/signing to record version, commit and architecture. The release tag must match `rockyVersion`.

macOS signing uses an installed Developer ID identity via `ROCKY_MAC_SIGN=true` and `ROCKY_MAC_SIGN_IDENTITY`; notarization uses a `notarytool` profile through `scripts/notarize-macos.sh`. Windows installer signing uses an installed certificate selected by `ROCKY_WINDOWS_CERT_THUMBPRINT` through `scripts/sign-windows.ps1`. These require publisher-owned credentials and validation on the target OS. Hosted CI does not automatically import certificates.

Back up the data directory with Rocky closed before upgrading. Verify notes, evidence, native version and credential migration after installation. Clean installation, upgrade, real Twitch, Kick and YouTube OAuth, Kick webhooks, AI/voice, system credential storage, OBS and long streams remain manual release gates.
