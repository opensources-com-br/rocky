const content = {
  group: "Avançado",
  lead: "Plugins recebem os eventos do Rocky e podem devolver ações. Servem para ligar o app ao resto da sua produção: overlays, bots, automação de cena.",
  blocks: [
    {"type": "h2", "text": "Anatomia de um plugin"},
    {"type": "code", "file": "~/.rocky/plugins/overlay.js", "text": "export default {\n  name: \"overlay\",\n  on: {\n    note(note) {\n      // manda a nota para um overlay local\n      fetch(\"http://localhost:4455/note\", {\n        method: \"POST\",\n        body: JSON.stringify(note),\n      });\n    },\n    superchat(sc) {\n      return { speak: `Obrigado, ${sc.user}!` };\n    },\n  },\n};"},
    {"type": "h2", "text": "Eventos disponíveis"},
    {"type": "table", "cols": "minmax(0,1fr) minmax(0,2fr)", "head": ["EVENTO", "QUANDO DISPARA"], "rows": [["message", "Cada mensagem que entra na fila unificada"], ["intervention", "Rocky decidiu falar algo"], ["note", "Uma nota foi registrada"], ["superchat", "Contribuição recebida em qualquer plataforma"], ["session", "Início e fim de sessão"]]},
    {"type": "h2", "text": "Ações que um plugin pode devolver"},
    {"type": "list", "items": ["speak: fazer o Rocky dizer algo no seu monitor.", "send: enviar mensagem ao chat nas plataformas com escrita permitida.", "note: registrar uma nota com etiqueta própria.", "suppress: descartar o evento antes de chegar ao agente."]},
    {"type": "note", "text": "Plugins rodam no mesmo processo do app, sem sandbox. Leia o código antes de instalar um plugin de terceiros."},
  ],
};

export default content;
