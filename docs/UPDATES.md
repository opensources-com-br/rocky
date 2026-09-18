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

