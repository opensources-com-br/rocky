import test from "node:test";
import assert from "node:assert/strict";
import { buildFile } from "../components/showcase/build-file.js";
const form = { name: 'Canal: "João"', category: "Programação", platforms: ["Twitch"], urls: { Twitch: "https://twitch.tv/joao" }, provider: "ollama", model: "llama3.1:8b", voice: "system", quote: "Minha frase", desc: "Minha live", story: "Primeiro parágrafo.\n\nSegundo parágrafo.", changes: "Mais tempo\nMenos repetição" };
test("preserves accented content and safely quotes front matter", () => {
  const file = buildFile(form);
  assert.equal(file.filename, "showcase/canal-joao.md");
  assert.ok(file.text.includes("name: " + JSON.stringify(form.name)));
  assert.ok(file.text.includes('ai: "ollama / llama3.1:8b"'));
  assert.ok(file.text.includes("Primeiro parágrafo.\n\nSegundo parágrafo."));
  assert.ok(file.text.includes("- Mais tempo\n- Menos repetição"));
});
test("falls back to a usable filename and keeps line breaks inside quoted metadata", () => {
  const file = buildFile({ ...form, name: "🎮", model: "line one\nline two" });
  assert.equal(file.filename, "showcase/seu-canal.md");
  assert.ok(file.text.includes('ai: "ollama / line one\\nline two"'));
});
