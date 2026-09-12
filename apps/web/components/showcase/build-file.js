export function buildFile(form) {
    const f = form;
    const slug = (f.name || "seu-canal").toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "") || "seu-canal";
    const story = (f.story || "").split(/\n{2,}/).filter(Boolean);
    const changes = (f.changes || "").split("\n").map((l) => l.trim()).filter(Boolean);
    const lines = [
      "---",
      "name: " + JSON.stringify(f.name || ""),
      "category: " + JSON.stringify(f.category),
      "platforms: " + JSON.stringify(f.platforms),
      "links:",
    ].concat(f.platforms.map((p) => "  - " + p.toLowerCase() + ": " + JSON.stringify((f.urls || {})[p] || ""))).concat([
      "ai: " + JSON.stringify(f.provider + (f.model ? " / " + f.model : "")),
      "voice: " + JSON.stringify(f.voice),
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
