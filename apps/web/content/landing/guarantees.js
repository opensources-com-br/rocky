const portuguese = [
  {
    "title": "Sem conta Rocky",
    "text": "Você autoriza Twitch, Kick, YouTube ou Facebook e configura a IA. Não existe cadastro ou assinatura do Rocky."
  },
  {
    "title": "Sem backend próprio",
    "text": "O app usa as APIs oficiais das plataformas e do provedor escolhido. A Kick entrega o chat por webhook HTTPS."
  },
  {
    "title": "MIT",
    "text": "Código aberto, auditável e livre para modificar e redistribuir sob a licença MIT."
  },
  {
    "title": "Registros locais",
    "text": "Notas, ideias, perguntas e resumos ficam em SQLite no computador, com backup e exportação. O banco não é criptografado pelo Rocky."
  }
];

const english = [
  {
    "title": "No Rocky account",
    "text": "Authorize Twitch, Kick, YouTube, or Facebook and configure AI. Rocky has no registration or subscription."
  },
  {
    "title": "No proprietary backend",
    "text": "The app uses official platform and provider APIs. Kick delivers chat through an HTTPS webhook."
  },
  {
    "title": "MIT",
    "text": "Open source, auditable, and free to modify and redistribute under the MIT license."
  },
  {
    "title": "Local records",
    "text": "Notes, ideas, questions, and summaries stay in SQLite on your computer, with backup and export. Rocky does not encrypt the database."
  }
];

export const getGuarantees = locale => locale === "pt-BR" ? portuguese : english;
