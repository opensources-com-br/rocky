import assert from "node:assert/strict";
import { readdir } from "node:fs/promises";
import test from "node:test";
import { DEFAULT_LOCALE, localized, localizedMetadata, normalizeLocale } from "../lib/i18n.js";

test("defaults unknown locales to English", () => {
  assert.equal(DEFAULT_LOCALE, "en");
  assert.equal(normalizeLocale(undefined), "en");
  assert.equal(normalizeLocale("es"), "en");
  assert.equal(localized("en", "English", "Português"), "English");
});

test("normalizes Brazilian Portuguese locale variants", () => {
  assert.equal(normalizeLocale("pt"), "pt-BR");
  assert.equal(normalizeLocale("PT-br"), "pt-BR");
  assert.equal(localized("pt-BR", "English", "Português"), "Português");
});

test("provides metadata in both languages", () => {
  assert.match(localizedMetadata("en", "/").title, /The chat has a voice/);
  assert.match(localizedMetadata("pt-BR", "/").title, /O chat ganhou voz/);
  assert.match(localizedMetadata("en", "/docs/").description, /installation/);
  assert.match(localizedMetadata("pt-BR", "/docs/").description, /instalação/);
});

test("has one English document for every Portuguese document", async () => {
  const docs = (await readdir(new URL("../content/docs/", import.meta.url))).filter(name => name.endsWith(".js") && name !== "index.js").sort();
  const englishDocs = (await readdir(new URL("../content/docs/en/", import.meta.url))).filter(name => name.endsWith(".js")).sort();
  assert.deepEqual(englishDocs, docs);

  for (const name of englishDocs) {
    const { default: page } = await import(new URL(`../content/docs/en/${name}`, import.meta.url));
    assert.ok(page.title, `${name} must have a title`);
    assert.ok(page.lead, `${name} must have a lead`);
    assert.ok(page.blocks.length, `${name} must have content blocks`);
  }
});
