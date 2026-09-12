const content = {
  group: "Recursos",
  lead: "Superchats, bits, presentes e estrelas entram numa fila própria e o Rocky lê em voz alta: nome, valor e mensagem.",
  blocks: [
    {"type": "h2", "text": "A fila"},
    {"type": "para", "text": "Contribuições aparecem na aba Superchats em ordem de chegada, com a plataforma e o valor. Quem ainda não foi lido fica destacado; ler marca como lido e registra o turno na aba Conversa."},
    {"type": "h2", "text": "Leitura automática ou manual"},
    {"type": "para", "text": "Com leitura automática, o Rocky lê cada contribuição assim que ela chega, respeitando a sua configuração de interrupção. Sem ela, a fila espera e você dispara com ⌥ ⇧ R quando houver uma brecha."},
    {"type": "h2", "text": "Valor mínimo"},
    {"type": "para", "text": "Em lives com muitas contribuições pequenas, definir um valor mínimo para leitura automática evita que o Rocky passe a live inteira lendo. O que fica abaixo do corte continua na fila para leitura manual."},
    {"type": "code", "file": "~/.rocky/config.toml", "text": "[superchats]\nauto_read   = true\nmin_value   = 10.0\ncurrency    = \"BRL\"\nannounce_platform = false"},
    {"type": "note", "text": "Rocky lê a mensagem do espectador literalmente. Se você preferir que ele resuma contribuições longas, ligue summarize_long na mesma seção."},
  ],
};

export default content;
