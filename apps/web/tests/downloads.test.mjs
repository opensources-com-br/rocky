import assert from "node:assert/strict";
import test from "node:test";
import { detectDesktopPlatform, normalizeArchitecture, selectInstaller } from "../lib/downloads.js";

const asset = name => ({ name, browser_download_url: `https://example.test/${name}` });

test("detects supported desktop platforms", () => {
  assert.equal(detectDesktopPlatform("macOS"), "macos");
  assert.equal(detectDesktopPlatform("Win32"), "windows");
  assert.equal(detectDesktopPlatform("", "Mozilla/5.0 (Windows NT 10.0)"), "windows");
  assert.equal(detectDesktopPlatform("Linux"), "unknown");
});

test("normalizes installer architectures", () => {
  assert.equal(normalizeArchitecture("aarch64"), "arm64");
  assert.equal(normalizeArchitecture("x86_64"), "x64");
  assert.equal(normalizeArchitecture(""), "unknown");
});

test("selects installers from the newest non-draft release", () => {
  const releases = [
    { draft: true, assets: [asset("unpublished.dmg")] },
    { draft: false, prerelease: true, assets: [asset("Rocky-1.0.10.dmg"), asset("Rocky-1.0.10.exe"), asset("Rocky-1.0.10.msi")] },
  ];
  assert.equal(selectInstaller(releases, "macos")?.name, "Rocky-1.0.10.dmg");
  assert.equal(selectInstaller(releases, "windows")?.name, "Rocky-1.0.10.msi");
});

test("matches architecture when releases provide multiple packages", () => {
  const releases = [{ draft: false, assets: [asset("Rocky-darwin-arm64.dmg"), asset("Rocky-darwin-x86_64.dmg")] }];
  assert.equal(selectInstaller(releases, "macos", "arm64")?.name, "Rocky-darwin-arm64.dmg");
  assert.equal(selectInstaller(releases, "macos", "x64")?.name, "Rocky-darwin-x86_64.dmg");
  assert.equal(selectInstaller(releases, "macos", "unknown"), null);
});

test("returns no installer for unsupported systems", () => {
  assert.equal(selectInstaller([{ draft: false, assets: [asset("Rocky.dmg")] }], "unknown"), null);
  assert.equal(selectInstaller([], "macos"), null);
});
