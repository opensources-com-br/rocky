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
    assert.equal(options.credentials, "omit");
    assert.equal(options.signal, controller.signal);
    return { ok: true, json: async () => newer };
  });
  assert.deepEqual(result, newer);
});

test("failed deployment responses never trigger a reload notice", async () => {
  const result = await checkSiteUpdate(current, "", undefined, async () => ({ ok: false }));
  assert.equal(result, null);
  await assert.rejects(checkSiteUpdate(current, "", undefined, async () => {
    throw new Error("offline");
  }), /offline/);
});

test("explicit reload keeps the route, query, and fragment with a fresh cache key", () => {
  const url = new URL(siteReloadUrl("https://rocky.test/rocky/docs/?topic=installation#updates", "commit-b"));
  assert.equal(url.pathname, "/rocky/docs/");
  assert.equal(url.searchParams.get("topic"), "installation");
  assert.equal(url.searchParams.get("rocky-site-version"), "commit-b");
  assert.equal(url.hash, "#updates");
});
