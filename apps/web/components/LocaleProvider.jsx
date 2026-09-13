"use client";

import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { usePathname } from "next/navigation";
import { DEFAULT_LOCALE, LOCALE_STORAGE_KEY, localizedMetadata, normalizeLocale } from "@/lib/i18n";

const LocaleContext = createContext(null);

export default function LocaleProvider({ children }) {
  const [locale, setLocaleState] = useState(DEFAULT_LOCALE);
  const pathname = usePathname();

  useEffect(() => {
    const storedLocale = window.localStorage.getItem(LOCALE_STORAGE_KEY);
    if (!storedLocale) return undefined;
    const restoreLocale = window.setTimeout(() => setLocaleState(normalizeLocale(storedLocale)), 0);
    return () => window.clearTimeout(restoreLocale);
  }, []);

  useEffect(() => {
    const metadata = localizedMetadata(locale, pathname);
    document.documentElement.lang = locale;
    document.title = metadata.title;
    document.querySelector('meta[name="description"]')?.setAttribute("content", metadata.description);
  }, [locale, pathname]);

  const value = useMemo(() => ({
    locale,
    setLocale(nextLocale) {
      const normalized = normalizeLocale(nextLocale);
      window.localStorage.setItem(LOCALE_STORAGE_KEY, normalized);
      setLocaleState(normalized);
    },
  }), [locale]);

  return <LocaleContext.Provider value={value}>{children}</LocaleContext.Provider>;
}

export function useLocale() {
  const context = useContext(LocaleContext);
  if (!context) throw new Error("useLocale must be used inside LocaleProvider");
  return context;
}
