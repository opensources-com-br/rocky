export const SITE_BUILD = {
  version: process.env.NEXT_PUBLIC_SITE_VERSION,
  builtAt: Number(process.env.NEXT_PUBLIC_SITE_BUILT_AT),
};

export function isNewerSiteBuild(current, candidate) {
  return typeof candidate?.version === "string"
    && /^[a-zA-Z0-9._:-]{1,160}$/.test(candidate.version)
    && candidate.version !== current.version
    && Number.isSafeInteger(candidate.builtAt)
    && candidate.builtAt > current.builtAt;
}

export async function checkSiteUpdate(current, basePath, signal, fetcher = fetch) {
  const response = await fetcher(`${basePath}/version.json?check=${Date.now()}`, {
    cache: "no-store", credentials: "omit", signal,
  });
  if (!response.ok) return null;
  const candidate = await response.json();
  return isNewerSiteBuild(current, candidate) ? candidate : null;
