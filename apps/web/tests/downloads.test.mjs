import assert from "node:assert/strict";
import test from "node:test";
import { detectDesktopPlatform, findInstaller, normalizeArchitecture, RELEASES_API, selectInstaller } from "../lib/downloads.js";

const asset = (name, tag = "v1.0.11") => ({ name, browser_download_url: `https://github.com/opensources-com-br/rocky/releases/download/${tag}/${name}` });
const packageAsset = (version, system, architecture, extension) => asset(`Rocky-${version}-${system}-${architecture}.${extension}`, `v${version}`);
const checksum = asset("SHA256SUMS.txt");

test("detects supported desktop platforms", () => {
  assert.equal(detectDesktopPlatform("macOS"), "macos");
  assert.equal(detectDesktopPlatform("Win32"), "windows");
  assert.equal(detectDesktopPlatform("", "Mozilla/5.0 (Windows NT 10.0)"), "windows");
  assert.equal(detectDesktopPlatform("Linux"), "unknown");
});

test("normalizes installer architectures", () => {
  assert.equal(normalizeArchitecture("aarch64"), "arm64");
  assert.equal(normalizeArchitecture("x86_64"), "x64");
  assert.equal(normalizeArchitecture("x86"), "unknown");
  assert.equal(normalizeArchitecture("x86", "64"), "x64");
  assert.equal(normalizeArchitecture("arm", "64"), "arm64");
  assert.equal(normalizeArchitecture("arm", "32"), "unknown");
  assert.equal(normalizeArchitecture(""), "unknown");
});

test("selects installers from the current non-draft release", () => {
  const releases = [
    { draft: true, assets: [asset("unpublished.dmg")] },
    { tag_name: "v1.0.11-alpha.1", draft: false, prerelease: true, assets: [checksum,
      packageAsset("1.0.11-alpha.1", "darwin", "universal", "dmg"),
      packageAsset("1.0.11-alpha.1", "windows", "amd64", "exe"),
      packageAsset("1.0.11-alpha.1", "windows", "amd64", "msi")] },
    { tag_name: "v1.0.10-alpha.17", draft: false, prerelease: true, assets: [asset("Rocky-1.0.10.dmg"), asset("Rocky-1.0.10.exe"), asset("Rocky-1.0.10.msi")] },
  ];
  assert.equal(selectInstaller(releases, "macos")?.name, "Rocky-1.0.11-alpha.1-darwin-universal.dmg");
  assert.equal(selectInstaller(releases, "windows", "x64")?.name, "Rocky-1.0.11-alpha.1-windows-amd64.msi");
});

test("rejects installers published before the icon and signing fixes", () => {
  const releases = [{ tag_name: "v1.0.10-alpha.17", draft: false, assets: [checksum, packageAsset("1.0.10-alpha.17", "darwin", "universal", "dmg")] }];
  assert.equal(selectInstaller(releases, "macos"), null);
});

test("matches architecture when releases provide multiple packages", () => {
  const releases = [{ tag_name: "v1.0.11", draft: false, assets: [checksum,
    packageAsset("1.0.11", "darwin", "arm64", "dmg"), packageAsset("1.0.11", "darwin", "x86_64", "dmg")] }];
  assert.equal(selectInstaller(releases, "macos", "arm64")?.name, "Rocky-1.0.11-darwin-arm64.dmg");
  assert.equal(selectInstaller(releases, "macos", "x64")?.name, "Rocky-1.0.11-darwin-x86_64.dmg");
  assert.equal(selectInstaller(releases, "macos", "unknown"), null);
});

test("never substitutes an incompatible or unidentified architecture", () => {
  const releases = [{ tag_name: "v1.0.11", assets: [checksum, packageAsset("1.0.11", "darwin", "arm64", "dmg")] }];
  assert.equal(selectInstaller(releases, "macos", "x64"), null);
  assert.equal(selectInstaller(releases, "macos", "unknown"), null);
  assert.equal(selectInstaller(releases, "macos", "arm64")?.name, "Rocky-1.0.11-darwin-arm64.dmg");
});

test("skips incomplete and draft releases until publication finishes", () => {
  const releases = [
    { tag_name: "v1.0.13", assets: [packageAsset("1.0.13", "darwin", "universal", "dmg")] },
    { tag_name: "v1.0.12", draft: true, assets: [checksum, packageAsset("1.0.12", "darwin", "universal", "dmg")] },
    { tag_name: "v1.0.11", assets: [checksum, packageAsset("1.0.11", "darwin", "universal", "dmg")] },
  ];
  assert.equal(selectInstaller(releases, "macos")?.name, "Rocky-1.0.11-darwin-universal.dmg");
});

test("also accepts checksum manifests without a text extension", () => {
  const releases = [{ tag_name: "v1.0.11", assets: [asset("SHA256SUMS"), packageAsset("1.0.11", "darwin", "universal", "dmg")] }];
  assert.equal(selectInstaller(releases, "macos")?.name, "Rocky-1.0.11-darwin-universal.dmg");
});

test("rejects unofficial download URLs and malformed API responses", () => {
  const releases = [{ tag_name: "v1.0.11", assets: [checksum,
    { ...packageAsset("1.0.11", "darwin", "universal", "dmg"), browser_download_url: "https://example.test/Rocky.dmg" }] }];
  assert.equal(selectInstaller(releases, "macos"), null);
  assert.equal(selectInstaller({ message: "rate limited" }, "macos"), null);
  assert.equal(selectInstaller([{ tag_name: "latest", assets: [checksum] }], "macos"), null);
});

test("rejects packages for another release, system, tag URL, or unknown CPU", () => {
  for (const candidate of [packageAsset("1.0.12", "darwin", "universal", "dmg"),
    packageAsset("1.0.11", "windows", "universal", "dmg"),
    asset("Rocky-1.0.11-darwin-universal.dmg", "v1.0.12"),
    packageAsset("1.0.11", "darwin", "armv7", "dmg")]) {
    assert.equal(selectInstaller([{ tag_name: "v1.0.11", assets: [checksum, candidate] }], "macos"), null);
  }
});

test("checks fresh release metadata and propagates API failures for the fallback", async () => {
  await assert.rejects(findInstaller("macos", "arm64", async (url, options) => {
    assert.equal(url, RELEASES_API);
    assert.equal(options.cache, "no-store");
    assert.ok(options.signal);
    return { ok: false, status: 403 };
  }), /403/);
});

test("returns no installer for unsupported systems", () => {
  assert.equal(selectInstaller([{ draft: false, assets: [asset("Rocky.dmg")] }], "unknown"), null);
  assert.equal(selectInstaller([], "macos"), null);
});
