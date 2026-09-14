const portuguese = [
  {
    "title": "Sem conta Rocky",
    "text": "Você conecta Twitch, Kick, YouTube, Facebook ou TikTok LIVE e configura a IA. Não existe cadastro ou assinatura do Rocky."
  },
  {
    "title": "Sem backend próprio",
    "text": "Twitch, Kick, YouTube e Facebook usam integrações oficiais. O TikTok usa WebCast não oficial via Eulerstream; a Kick entrega o chat por webhook HTTPS."
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
    "text": "Connect Twitch, Kick, YouTube, Facebook, or TikTok LIVE and configure AI. Rocky has no registration or subscription."
  },
  {
    "title": "No proprietary backend",
    "text": "Twitch, Kick, YouTube, and Facebook use official integrations. TikTok uses unofficial WebCast through Eulerstream; Kick delivers chat through an HTTPS webhook."
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
