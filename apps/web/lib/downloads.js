export const RELEASES_API = "https://api.github.com/repos/opensources-com-br/rocky/releases?per_page=10";
export const RELEASES_PAGE = "https://github.com/opensources-com-br/rocky/releases";
const MINIMUM_INSTALLER_VERSION = [1, 0, 11];

export function detectDesktopPlatform(platform = "", userAgent = "") {
  const value = `${platform} ${userAgent}`.toLowerCase();
  if (value.includes("mac")) return "macos";
  if (value.includes("win")) return "windows";
  return "unknown";
}

export function normalizeArchitecture(value = "", bitness = "") {
  const architecture = value.toLowerCase();
  if (architecture === "arm" && bitness === "64") return "arm64";
  if (architecture === "x86" && bitness === "64") return "x64";
  if (["arm64", "aarch64"].includes(architecture)) return "arm64";
  if (["x86_64", "amd64", "x64"].includes(architecture)) return "x64";
  return "unknown";
}

function assetArchitecture(name) {
  const architecture = name.toLowerCase().match(/-(arm64|aarch64|x86_64|amd64|x64|universal)\.(?:dmg|msi|exe)$/)?.[1];
  return architecture === "universal" ? "universal" : normalizeArchitecture(architecture);
}

function isCurrentInstallerRelease(release) {
  const match = release.tag_name?.match(/^v?(\d+)\.(\d+)\.(\d+)(?:-[\da-z.-]+)?$/i);
  if (!match) return false;

  const version = match.slice(1).map(Number);
  for (let index = 0; index < version.length; index += 1) {
    if (version[index] !== MINIMUM_INSTALLER_VERSION[index]) {
      return version[index] > MINIMUM_INSTALLER_VERSION[index];
    }
  }
  return true;
}

export function selectInstaller(releases, platform, architecture = "unknown") {
  if (!Array.isArray(releases)) return null;
  const release = releases.find(item => item && !item.draft && isCurrentInstallerRelease(item)
    && Array.isArray(item.assets) && item.assets.some(asset => /^SHA256SUMS(?:\.txt)?$/.test(asset?.name)));
  if (!release || !["macos", "windows"].includes(platform)) return null;

  const extensions = platform === "macos" ? [".dmg"] : [".msi", ".exe"];
  const system = platform === "macos" ? "darwin" : "windows";
  const prefix = `Rocky-${release.tag_name.replace(/^v/i, "")}-${system}-`;
  for (const extension of extensions) {
    const assets = release.assets.filter(asset => typeof asset?.name === "string"
      && asset.name.startsWith(prefix)
      && asset.name.toLowerCase().endsWith(extension)
      && assetArchitecture(asset.name) !== "unknown"
      && isOfficialAsset(asset, release.tag_name));
    const universal = assets.find(asset => assetArchitecture(asset.name) === "universal");
    if (universal) return universal;
    const matching = assets.find(asset => assetArchitecture(asset.name) === architecture);
    if (matching) return matching;
  }
  return null;
}

function isOfficialAsset(asset, tag) {
  try {
    const url = new URL(asset.browser_download_url);
    return url.origin === "https://github.com"
      && decodeURIComponent(url.pathname) === `/opensources-com-br/rocky/releases/download/${tag}/${asset.name}`;
  } catch { return false; }
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
