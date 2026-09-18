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
  const releases = [{ tag_name: "v1.0.11-alpha.1", draft: false, assets: [asset("Rocky-darwin-arm64.dmg"), asset("Rocky-darwin-x86_64.dmg")] }];
  assert.equal(selectInstaller(releases, "macos", "arm64")?.name, "Rocky-darwin-arm64.dmg");
  assert.equal(selectInstaller(releases, "macos", "x64")?.name, "Rocky-darwin-x86_64.dmg");
  assert.equal(selectInstaller(releases, "macos", "unknown"), null);
});

test("returns no installer for unsupported systems", () => {
  assert.equal(selectInstaller([{ draft: false, assets: [asset("Rocky.dmg")] }], "unknown"), null);
  assert.equal(selectInstaller([], "macos"), null);
});
