# Validação desktop

Este documento acompanha a validação do build e da janela nativa do Rocky. A versão principal está em [VALIDATION.md](VALIDATION.md).

## Validação automatizada

Cada push na `main` e cada pull request executa o build Gradle completo no macOS e no Windows. O workflow também cria instaladores nativos e os disponibiliza como artefatos do GitHub Actions por sete dias:

| Runner | Tarefas | Artefato |
| --- | --- | --- |
| `macos-latest` | `build`, `packageDmg` | `rocky-macos`, contendo um `.dmg` |
| `windows-latest` | `build`, `packageMsi`, `packageExe` | `rocky-windows`, contendo instaladores `.msi` e `.exe` |

Os pacotes ainda são builds de desenvolvimento sem assinatura. A assinatura e a notarização do macOS fazem parte da etapa de lançamento.

A suíte automatizada executa um teste prolongado da Twitch com 20.000 mensagens e sete quedas simuladas. Os testes da Kick cobrem payloads OAuth, entrega assinada de webhook, deduplicação, estado limitado, Pulso e preferências seguras. OAuth real e encaminhamento público da Kick continuam manuais.

## Comandos locais

Use o JDK 17 e execute:

```shell
./gradlew build
./gradlew :apps:desktop:run
./gradlew :apps:desktop:packageDmg
```

No PowerShell do Windows:

```powershell
.\gradlew.bat build
.\gradlew.bat :apps:desktop:run
.\gradlew.bat :apps:desktop:packageMsi :apps:desktop:packageExe
```

Os pacotes são gerados em `apps/desktop/build/compose/binaries/main/`.

## Teste rápido da janela nativa

Execute esta lista no macOS e em uma instalação ou emulador Windows real. Teste o aplicativo empacotado, pois ele contém o mesmo runtime e os mesmos metadados do artefato do instalador.

- [ ] Instalar e abrir o Rocky sem uma instalação do Java no sistema.
- [ ] Mover a janela pela barra de título nativa.
- [ ] Redimensionar a janela expandida e confirmar que ela não fica menor que 340 × 180.
- [ ] Minimizar pelo controle amarelo do Rocky e restaurar pelo Dock ou pela barra de tarefas.
- [ ] Fixar a janela, colocar outro aplicativo sobre ela e confirmar que o Rocky continua visível; desafixar e confirmar que o empilhamento normal retorna.
- [ ] Ativar o modo compacto e confirmar que a janela passa para 340 × 180.
- [ ] Voltar ao modo expandido e confirmar que o tamanho anterior é restaurado.
- [ ] Abrir as configurações e confirmar que a janela passa para 420 × 820; fechar e confirmar que o tamanho anterior retorna.
- [ ] Fechar o Rocky pelo controle vermelho.

## Teste com streamers

Use o [protocolo de teste alpha com streamers](STREAMER_TEST.pt-BR.md) para validar instalação limpa, live de duas horas, captura de janela e áudio pelo OBS, interrupção de rede e evidências. Registre cada execução pelo formulário de issue **Streamer alpha test**.

O teste prolongado automatizado não substitui esta etapa manual. Um candidato a lançamento continua sem validação até que os registros exigidos de macOS, Windows, OBS e lives reais sejam aprovados.

## Registro da validação

Registre cada execução manual em uma issue ou pull request usando este modelo:

```text
Data:
Commit do Rocky:
Sistema operacional e versão:
Arquitetura:
Resolução e escala da tela:
Pacote testado (.dmg, .msi ou .exe):
Resultado da lista: passou / falhou
Problemas encontrados:
```

Checkpoint automatizado histórico (10/09/2026): o build completo, os testes de interface, o teste de sessão prolongada e o empacotamento nativo passam nos runners macOS e Windows do GitHub. O protocolo com streamers continua sendo uma etapa manual e exige resultados registrados de lives reais.

Para identidade do candidato, assinatura e verificações manuais pendentes, consulte [preparação de lançamento](RELEASE_PREPARATION.md).
