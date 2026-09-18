"use client";

import { useEffect, useState } from "react";
import { useLocale } from "@/components/LocaleProvider";
import { localized } from "@/lib/i18n";
import { checkSiteUpdate, SITE_BUILD, siteReloadUrl } from "@/lib/site-updates";

export default function SiteUpdateNotice() {
  const { locale } = useLocale();
  const [update, setUpdate] = useState(null);
  const [dismissed, setDismissed] = useState(null);

  useEffect(() => {
    let active = true;
    let request;
    async function check() {
      if (request || document.visibilityState !== "visible" || !navigator.onLine) return;
      request = new AbortController();
      const timeout = window.setTimeout(() => request?.abort(), 10000);
      try {
        const next = await checkSiteUpdate(SITE_BUILD, process.env.NEXT_PUBLIC_BASE_PATH || "", request.signal);
        if (active && next) setUpdate(next);
      } catch { /* Keep the current site usable while offline or during deployment. */ }
      finally { window.clearTimeout(timeout); request = null; }
    }
    check();
    const interval = window.setInterval(check, 300000);
    window.addEventListener("online", check);
    window.addEventListener("focus", check);
    document.addEventListener("visibilitychange", check);
    return () => {
      active = false;
      request?.abort();
      window.clearInterval(interval);
      window.removeEventListener("online", check);
      window.removeEventListener("focus", check);
      document.removeEventListener("visibilitychange", check);
    };
  }, []);

