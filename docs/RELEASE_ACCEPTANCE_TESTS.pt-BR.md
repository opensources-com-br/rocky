# Fase 16 — aceite do candidato

Congele um único commit e use os pacotes produzidos por ele em toda a homologação.
Registre tag, versão lógica, versão nativa, commit, SO, arquitetura e responsável.

## Pacotes e instalação

- Gere DMG no macOS e MSI/EXE no Windows a partir de checkout limpo com JDK 17.
- Confirme que tag, `rockyVersion`, versão nativa e `BUILDINFO` correspondem.
- Confira nome, arquitetura e SHA-256 de cada instalador publicado.
- Baixe novamente os artefatos da release; não teste somente a pasta de build.
- Instale em usuário limpo e máquina sem JDK adicional.
- Abra pelo Finder ou menu Iniciar e confirme tray/menu bar e ícone corretos.
- Registre assinatura, notarização e qualquer aviso do sistema operacional.
- Feche, reabra, reinicie o computador e confirme inicialização normal.
- Desinstale e verifique atalhos, processos e entradas restantes.
- Repita separadamente em macOS e Windows nas arquiteturas publicadas.
