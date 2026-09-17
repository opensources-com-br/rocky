# Homologação dos provedores de IA

Execute com contas de teste, sem usar chaves de produção, e registre provedor, modelo, horário e resultado.

## Fluxo comum

- [ ] Testar chave válida, inválida, revogada e sem permissão.
- [ ] Testar modelo inexistente, antigo, atual e sem acesso.
- [ ] Testar conta sem créditos, quota esgotada e rate limit.
- [ ] Testar timeout, DNS indisponível e resposta HTTP 5xx.
- [ ] Cancelar uma geração lenta e iniciar outra imediatamente.
- [ ] Confirmar que a geração cancelada nunca aparece depois.
- [ ] Gerar em PT-BR e inglês, com e sem mensagens recentes.
- [ ] Pedir “explica melhor” após uma resposta anterior.
- [ ] Priorizar um pedido direto durante análise automática.
- [ ] Confirmar que a análise manual e automática funcionam.
- [ ] Validar sugestão vazia, JSON inválido e resposta truncada.
- [ ] Validar recusa de segurança sem perder o estado da tela.

## Fontes, tokens e segurança

- [ ] Aceitar somente IDs presentes nas mensagens enviadas.
- [ ] Rejeitar fontes inventadas, ausentes ou em formato inválido.
- [ ] Confirmar o JSON Schema enviado a cada provedor.
- [ ] Comparar os tokens exibidos com a resposta bruta da API.
- [ ] Registrar quando o provedor omitir a contagem de tokens.
- [ ] Inspecionar logs, erros e diagnósticos em busca da chave.
- [ ] Confirmar que a chave aparece apenas no header autorizado.
