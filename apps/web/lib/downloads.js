export const RELEASES_API = "https://api.github.com/repos/opensources-com-br/rocky/releases?per_page=10";
export const RELEASES_PAGE = "https://github.com/opensources-com-br/rocky/releases";
const MINIMUM_INSTALLER_VERSION = [1, 0, 11];

export function detectDesktopPlatform(platform = "", userAgent = "") {
  const value = `${platform} ${userAgent}`.toLowerCase();
  if (value.includes("mac")) return "macos";
  if (value.includes("win")) return "windows";
  return "unknown";
}

export function normalizeArchitecture(value = "") {
  const architecture = value.toLowerCase();
  if (["arm64", "aarch64"].includes(architecture)) return "arm64";
  if (["x86", "x86_64", "amd64", "x64"].includes(architecture)) return "x64";
  return "unknown";
}

function assetArchitecture(name) {
  const normalized = name.toLowerCase();
  if (/(arm64|aarch64)/.test(normalized)) return "arm64";
  if (/(x86_64|amd64|x64)/.test(normalized)) return "x64";
  return "universal";
}

function isCurrentInstallerRelease(release) {
  const match = release.tag_name?.match(/^v?(\d+)\.(\d+)\.(\d+)/i);
  if (!match) return true;

  const version = match.slice(1).map(Number);
  for (let index = 0; index < version.length; index += 1) {
    if (version[index] !== MINIMUM_INSTALLER_VERSION[index]) {
      return version[index] > MINIMUM_INSTALLER_VERSION[index];
    }
  }
  return true;
}

export function selectInstaller(releases, platform, architecture = "unknown") {
  const release = releases.find(item => !item.draft && isCurrentInstallerRelease(item));
  if (!release || !["macos", "windows"].includes(platform)) return null;

  const extensions = platform === "macos" ? [".dmg"] : [".msi", ".exe"];
  for (const extension of extensions) {
    const assets = release.assets.filter(asset => asset.name.toLowerCase().endsWith(extension));
    const universal = assets.find(asset => assetArchitecture(asset.name) === "universal");
    if (universal) return universal;
    const matching = assets.find(asset => assetArchitecture(asset.name) === architecture);
    if (matching) return matching;
    if (assets.length === 1) return assets[0];
  }
  return null;
}

export async function findInstaller(platform, architecture = "unknown", fetcher = fetch) {
  const response = await fetcher(RELEASES_API, {
    headers: {
      Accept: "application/vnd.github+json",
      "X-GitHub-Api-Version": "2022-11-28",
    },
  });
  if (!response.ok) throw new Error(`GitHub releases request failed: ${response.status}`);
  return selectInstaller(await response.json(), platform, architecture);
}
