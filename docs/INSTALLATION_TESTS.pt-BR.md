# Homologação de instalação

Execute esta lista com os artefatos publicados, nunca com uma build local. Anexe versão do SO, arquitetura, nome do arquivo, checksum e capturas ao resultado.

## macOS

- [ ] Instalar o DMG em um usuário sem Java e abrir pelo Finder.
- [ ] Confirmar versão, commit e arquitetura exibidos em Settings.
- [ ] Abrir o app a partir de um caminho com espaços e caracteres acentuados.
- [ ] Abrir novamente e confirmar o comportamento de instância única.
- [ ] Remover o app e confirmar quais dados permanecem em `~/Library/Application Support/Rocky`.

## Windows 11

- [ ] Instalar MSI e EXE em uma máquina sem Java.
- [ ] Abrir pelo menu Iniciar e pelo atalho criado pelo instalador.
- [ ] Confirmar versão, commit e arquitetura exibidos em Settings.
- [ ] Instalar em um caminho com espaços e caracteres acentuados.
- [ ] Abrir novamente e confirmar o comportamento de instância única.
- [ ] Desinstalar e confirmar a remoção dos atalhos e a situação de `%APPDATA%\Rocky`.
