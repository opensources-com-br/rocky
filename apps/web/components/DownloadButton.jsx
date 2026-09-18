"use client";

import { useState } from "react";
import { detectDesktopPlatform, findInstaller, normalizeArchitecture, RELEASES_PAGE } from "@/lib/downloads";
import { localized } from "@/lib/i18n";

async function browserArchitecture() {
  try {
    const values = await navigator.userAgentData?.getHighEntropyValues?.(["architecture", "bitness"]);
    return normalizeArchitecture(values?.architecture, values?.bitness);
  } catch {
    return "unknown";
  }
}

export default function DownloadButton({ className, locale, style }) {
  const [loading, setLoading] = useState(false);

  async function download(event) {
    if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;
    event.preventDefault();
    if (loading) return;
    setLoading(true);
    try {
      const platform = detectDesktopPlatform(navigator.userAgentData?.platform || navigator.platform, navigator.userAgent);
      const installer = await findInstaller(platform, await browserArchitecture());
      if (installer) {
        window.location.assign(installer.browser_download_url);
      } else {
        window.location.assign(RELEASES_PAGE);
      }
    } catch {
      window.location.assign(RELEASES_PAGE);
    } finally {
      setLoading(false);
    }
  }

  return <a
    className={className}
    href={RELEASES_PAGE}
    aria-busy={loading}
    aria-live="polite"
    onClick={download}
    style={style}
  >
    {loading
      ? localized(locale, "Preparing download…", "Preparando download…")
      : localized(locale, "Download app", "Baixar aplicativo")}
  </a>;
}
