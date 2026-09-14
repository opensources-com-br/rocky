"use client";

import { useState } from "react";
import { detectDesktopPlatform, findInstaller, normalizeArchitecture, RELEASES_PAGE } from "@/lib/downloads";
import { localized } from "@/lib/i18n";

async function browserArchitecture() {
  try {
    const values = await navigator.userAgentData?.getHighEntropyValues?.(["architecture"]);
    return normalizeArchitecture(values?.architecture);
  } catch {
    return "unknown";
  }
}

export default function DownloadButton({ className, locale, style }) {
  const [loading, setLoading] = useState(false);
  const [unavailable, setUnavailable] = useState(false);

  async function download(event) {
    if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;
    event.preventDefault();
    if (loading) return;
    const downloadWindow = window.open(RELEASES_PAGE, "_blank");
    if (downloadWindow) downloadWindow.opener = null;
    setUnavailable(false);
    setLoading(true);
    try {
      const platform = detectDesktopPlatform(navigator.userAgentData?.platform || navigator.platform, navigator.userAgent);
      const installer = await findInstaller(platform, await browserArchitecture());
      if (installer) {
        downloadWindow?.location.replace(installer.browser_download_url);
      } else {
        setUnavailable(true);
        setLoading(false);
      }
    } catch {
      downloadWindow?.location.replace(RELEASES_PAGE);
    }
  }

  return <a
    className={className}
    href={RELEASES_PAGE}
    target="_blank"
    rel="noreferrer"
    aria-busy={loading}
    aria-live="polite"
    onClick={download}
    style={style}
  >
    {loading
      ? localized(locale, "Preparing download…", "Preparando download…")
      : unavailable
        ? localized(locale, "Signed build coming soon", "Versão assinada em breve")
        : localized(locale, "Download app", "Baixar aplicativo")}
  </a>;
}
