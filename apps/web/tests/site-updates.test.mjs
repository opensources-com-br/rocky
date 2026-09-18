import assert from "node:assert/strict";
import test from "node:test";
import { checkSiteUpdate, isNewerSiteBuild, siteReloadUrl } from "../lib/site-updates.js";

const current = { version: "commit-a", builtAt: 1000 };
const newer = { version: "commit-b", builtAt: 2000 };

test("announces only a valid, different, newer build", () => {
  assert.equal(isNewerSiteBuild(current, newer), true);
  for (const candidate of [null, {}, current, { ...newer, builtAt: 999 },
    { ...newer, builtAt: "2000" }, { ...newer, version: "<script>" }, { ...newer, version: "" }]) {
    assert.equal(isNewerSiteBuild(current, candidate), false);
  }
});

test("version requests respect project base paths and bypass caches", async () => {
  const controller = new AbortController();
  const result = await checkSiteUpdate(current, "/rocky", controller.signal, async (url, options) => {
    assert.match(url, /^\/rocky\/version\.json\?check=\d+$/);
    assert.equal(options.cache, "no-store");
