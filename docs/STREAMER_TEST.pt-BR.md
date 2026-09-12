# Teste alpha com streamers

Este protocolo valida o Rocky nas condições importantes durante uma live real. A versão principal está em [STREAMER_TEST.md](STREAMER_TEST.md).

Use o prerelease mais recente dos [Releases do GitHub](https://github.com/opensources-com-br/rocky/releases). Registre cada execução com o modelo no fim deste documento. Uma verificação só conta como aprovada quando sua evidência estiver registrada.

## Critério de liberação

Antes de promover um build alpha, conclua:

- uma instalação limpa em uma versão compatível do macOS;
- uma instalação limpa no Windows 11;
- pelo menos três sessões reais na Twitch com dois streamers diferentes;
- uma sessão contínua de pelo menos duas horas;
- uma gravação no OBS em cada sistema operacional;
- todos os cenários de interrupção de rede abaixo.

Travamentos, perda de notas, mistura de mensagens de um canal anterior, conexões irrecuperáveis com a Twitch e áudio do Rocky entrando inesperadamente na transmissão bloqueiam a liberação.

## 1. Instalação limpa

Use um snapshot de máquina virtual, um usuário novo do sistema operacional ou um computador onde o Rocky nunca tenha sido executado. Não instale um JDK separado.

1. Baixe o instalador e o `SHA256SUMS.txt` da mesma versão.
2. Verifique o checksum:

   ```shell
   shasum -a 256 Rocky-*.dmg
   ```

   ```powershell
   Get-FileHash .\Rocky-*.msi -Algorithm SHA256
   Get-FileHash .\Rocky-*.exe -Algorithm SHA256
   ```

3. Instale e abra o Rocky pelo Finder ou menu Iniciar do Windows.
4. Confirme que o guia inicial abre e acompanha o idioma do sistema operacional.
5. Alterne entre inglês e português, feche o Rocky, abra novamente e confirme que a escolha persiste.
6. Conclua as verificações obrigatórias de Twitch e IA. Confirme que **Concluir configuração** é liberado sem testar a voz; depois configure e teste a voz opcionalmente.
7. Feche e abra o Rocky novamente. Confirme que o guia permanece concluído e as notas locais continuam disponíveis.
8. Desinstale o Rocky pelo Finder ou pelos Aplicativos instalados do Windows. Confirme que nenhum atalho permanece.

Registre os alertas de segurança do sistema operacional. Os pacotes alpha ainda não possuem assinatura, portanto alertas são esperados; falha silenciosa ou um aplicativo que nunca abre não são comportamentos aceitáveis.

## 2. Sessão de live prolongada

Faça uma transmissão real na Twitch por pelo menos duas horas. Use uma segunda conta da Twitch para enviar mensagens no chat.

- Envie mensagens normais continuamente e uma rajada de pelo menos 20 mensagens em um minuto.
- Gere sugestões de IA manual e automaticamente durante toda a sessão.
- Salve, edite, exclua e exporte notas depois de pelo menos 30, 60 e 120 minutos.
- Teste a leitura por voz pelo menos uma vez a cada 30 minutos.
- Se o `whisper.cpp` estiver configurado, grave e transcreva pelo menos cinco comandos do streamer.
- Alterne entre as abas Conversa, Notas, Ideias e Pulso.
- Redimensione, fixe, minimize e use o modo compacto enquanto o OBS estiver aberto.
- Registre a memória usada pelo Rocky ao iniciar, depois de 30 minutos, 60 minutos e ao encerrar.

Critérios de aprovação:

- a interface continua responsiva e nenhuma reinicialização é necessária;
- novas mensagens continuam chegando em ordem;
- somente as 1.000 mensagens mais recentes permanecem na memória;
- a memória não cresce continuamente depois dos primeiros 30 minutos nem aumenta mais de 300 MB;
- notas salvas sobrevivem à reinicialização do app e o Markdown exportado abre corretamente;
- operações de voz e transcrição terminam ou são canceladas sem deixar os controles ocupados.

## 3. Validação com OBS

Faça uma gravação local curta antes da transmissão pública.

1. Adicione o Rocky por Captura de janela e confirme que os layouts completo e compacto aparecem sem piscar.
2. Fixe o Rocky sobre o OBS e outro aplicativo; depois desafixe e confirme que a ordem normal das janelas retorna.
3. Capture apenas o microfone do streamer. Acione **Testar voz** e confirme que o streamer ouve o Rocky, mas a gravação não.
4. Adicione intencionalmente a captura de áudio do desktop. Confirme que o Rocky passa a ser ouvido na gravação e documente essa escolha.
5. Reproduza uma sugestão enquanto o streamer fala. Confirme que isso não cria eco, microfonia ou transcrição repetida.
6. Ligue e desligue o ouvinte durante a gravação do OBS; interrompa uma resposta e faça outro comando. Confirme que o Rocky captura apenas o microfone escolhido.
7. Revise a gravação procurando áudio estourado, duplicado, microfone ausente e exposição acidental das configurações ou API keys.

Use fones de ouvido nos testes de voz. Não mostre a tela de configurações de IA em uma cena pública enquanto houver uma API key nela.

## 4. Interrupção da conexão

Mantenha uma segunda conta pronta para enviar uma mensagem única depois de cada recuperação.

1. Desative a rede por 15 segundos, restaure e envie `recovery-15`.
2. Desative a rede por 60 segundos, restaure e envie `recovery-60`.
3. Alterne entre Wi-Fi e Ethernet ou outra rede; depois envie `recovery-switch`.
4. Desconecte a Twitch pelo Rocky enquanto houver uma reconexão pendente. Aguarde 45 segundos e confirme que ele não reconecta sozinho.
5. Conecte novamente, feche e abra o Rocky. Confirme que uma nova autorização é solicitada porque os tokens ficam somente na memória.

Critérios de aprovação:

- o Rocky mostra o estado de reconexão depois da perda de conectividade;
- o chat retorna em até 60 segundos depois que a rede volta a funcionar;
- cada mensagem de recuperação aparece uma vez;
- mensagens recebidas antes da interrupção permanecem visíveis até serem removidas pelo limite de 1.000 mensagens;
- desconectar cancela a recuperação pendente e eventos da sessão antiga não entram na sessão nova;
- uma recuperação que falhar produz um erro visível e permite uma nova tentativa de conexão.

## Registro do teste

```text
Data e duração:
Pessoa responsável / streamer:
Versão e commit do Rocky:
Instalador e SHA-256:
Sistema operacional, versão e arquitetura:
Resolução e escala da tela:
Canal da Twitch:
Provedor e modelo de IA:
Voz e microfone:
Versão do OBS e fontes de captura:
Cenários de rede concluídos:
Memória em 0 / 30 / 60 / minuto final:
Verificações aprovadas:
Verificações reprovadas:
Links de logs, capturas ou gravação:
Issues criadas:
Resultado final: passou / falhou
```
