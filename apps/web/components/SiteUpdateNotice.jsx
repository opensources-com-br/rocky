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
