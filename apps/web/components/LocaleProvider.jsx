"use client";

import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { DEFAULT_LOCALE, LOCALE_STORAGE_KEY, normalizeLocale } from "@/lib/i18n";

const LocaleContext = createContext(null);

export default function LocaleProvider({ children }) {
  const [locale, setLocaleState] = useState(DEFAULT_LOCALE);

  useEffect(() => {
    const storedLocale = window.localStorage.getItem(LOCALE_STORAGE_KEY);
    if (storedLocale) setLocaleState(normalizeLocale(storedLocale));
  }, []);

  useEffect(() => {
    document.documentElement.lang = locale;
  }, [locale]);

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
