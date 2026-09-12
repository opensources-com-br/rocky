export function buildFile(form) {
    const f = form;
    const slug = (f.name || "seu-canal").toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
    const story = (f.story || "").split(/\n{2,}/).filter(Boolean);
    const changes = (f.changes || "").split("\n").map((l) => l.trim()).filter(Boolean);
    const lines = [
      "---",
      "name: " + (f.name || ""),
      "category: " + f.category,
      "platforms: [" + f.platforms.join(", ") + "]",
      "links:",
    ].concat(f.platforms.map((p) => "  - " + p.toLowerCase() + ": " + ((f.urls || {})[p] || ""))).concat([
      "ai: " + f.provider + (f.model ? " / " + f.model : ""),
      "voice: " + f.voice,
      "---",
      "",
      "# " + (f.name || "Seu canal"),
      "",
      "> " + (f.quote || ""),
      "",
      f.desc || "",
      "",
      "## Depoimento",
      "",
      story.length ? story.join("\n\n") : "",
      "",
      "## O que mudou",
      "",
      changes.length ? changes.map((c) => "- " + c).join("\n") : "",
      "",
    ]);
    return { filename: "showcase/" + slug + ".md", text: lines.join("\n") };
}
