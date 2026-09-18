# Atualização integrada / In-app updates

## Usar no desktop

A partir de **1.0.12-alpha.1**, o Rocky verifica releases oficiais ao abrir (opção em Configurações → Dados). O aviso **Atualizar** baixa o pacote correto para Windows x64, macOS Intel ou Apple Silicon. Também é possível usar **Verificar atualizações** nas configurações. Downloads têm progresso, cancelamento e verificação de tamanho e SHA-256.

Depois do download, **Atualizar e reiniciar** prepara o instalador, salva a sessão e encerra o app. Um processo separado aguarda o Rocky sair, instala a nova versão e abre o Rocky novamente. A instalação fica bloqueada enquanto houver uma plataforma conectada ou em conexão. O Windows pode solicitar autorização do sistema; o computador não é reiniciado automaticamente.

Notas, evidências, configurações, modelos de voz e credenciais não são apagados. O atualizador troca apenas os arquivos da aplicação. Se a sessão não puder ser salva ou o pacote falhar na preparação, o Rocky continua aberto.

No macOS, instale o app em `/Applications/Rocky.app` ou `~/Applications/Rocky.app`, com permissão de escrita no diretório. A execução pelo DMG ou pelo código-fonte não permite atualização integrada. O pacote é validado e preparado no mesmo volume, e a versão anterior é restaurada se a troca ou a abertura falhar. Se o app instalado tiver assinatura Developer ID, a atualização precisa ter o mesmo Team ID.

No Windows, a instalação deve ter sido feita pelo instalador oficial, com o mesmo identificador de upgrade. O MSI atualiza o diretório existente. A remoção da versão antiga ocorre dentro da transação do Windows Installer, permitindo rollback se a instalação falhar. Uma assinatura válida no executável instalado exige o mesmo publisher no pacote. O alpha sem certificado usa o checksum da release oficial.

Versões anteriores ao atualizador precisam receber este primeiro pacote pelo fluxo antigo ou pelo site, instalando sobre a versão existente. Não é necessário desinstalar. O novo botão passa a cuidar das versões seguintes.

## Recuperação

Falhas ficam registradas na subpasta `updates/install-*` do diretório de dados. O próximo início mostra o resultado em Configurações → Dados. `installation.log` registra o helper; no Windows, `msi.log` registra o instalador. A falha de preparação mantém o app aberto e permite tentar novamente. Se o Windows pedir reinício do sistema, o Rocky informa isso no próximo início.

O rollback cobre erros na troca e no comando de abertura; não monitora falhas depois que o novo processo inicia. Ele protege a substituição dos arquivos do app; ele não desfaz migrações do banco realizadas por uma versão que já abriu. O encerramento forçado do computador durante a instalação pode exigir reinstalar o pacote oficial sobre a instalação existente. Os dados permanecem em seu diretório separado.

## Site

O site consulta `version.json` e oferece **Recarregar site** quando detecta uma publicação mais recente. O usuário escolhe quando recarregar; a URL e o idioma são preservados. O site não instala o aplicativo desktop. Seus botões de download consultam releases publicadas com checksums e selecionam apenas uma arquitetura compatível.

## Publicar pelo GitHub

1. Atualize `rockyVersion` e `rockyPackageVersion` em `gradle.properties`. A parte numérica deve ser igual e maior que a de todos os releases publicados, inclusive para um novo alpha.
2. Valide os testes e os instaladores. Use JDK 17; a matriz do GitHub inclui macOS ARM64, macOS Intel e Windows x64.
3. Crie uma tag `v` seguida da versão exata. O workflow de release assina/notariza macOS e assina Windows quando os secrets opcionais estiverem configurados.
4. O pipeline valida os quatro instaladores e os três BUILDINFOs, gera `SHA256SUMS.txt`, envia tudo a um draft e só então publica. Um draft incompleto não aparece no atualizador. A repetição do job pode recuperar um draft, mas não sobrescreve um release público.

Tags `-alpha.N` criam prereleases; tags sem sufixo criam releases estáveis. Clientes estáveis ignoram alphas. A assinatura e os secrets estão documentados em [RELEASE_PREPARATION.md](RELEASE_PREPARATION.md). O site é publicado pelo workflow Site após alterações web em `main`.

## English

Starting with **1.0.12-alpha.1**, choose **Update**, then **Update and restart**, or use Settings → Data. Rocky downloads and verifies the matching official installer, saves the session, exits, updates the existing installation, and relaunches. Active platform sessions block installation. Notes, preferences, credentials, and voice models are preserved. Older versions need one update through their existing installer flow to gain this capability; uninstalling is unnecessary.

macOS requires a writable installation in Applications and preserves the existing Developer ID team. It stages the app on the same volume and restores the previous bundle if replacement or launch fails. Windows uses the existing MSI upgrade identity and installation location, with transactional rollback and an optional system permission prompt. It never automatically reboots the computer. Logs and the next-start result are available through Settings → Data.

The website separately offers **Reload site** after a newer deployment. Publishing requires a strictly increasing native version, complete packages for both Mac architectures and Windows x64, matching build metadata, and checksums. Only after uploading every asset does the GitHub workflow expose the release to clients.
