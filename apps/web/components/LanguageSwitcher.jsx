"use client";

import { useLocale } from "@/components/LocaleProvider";
import { Button } from "@/components/ui/button";
import { localized } from "@/lib/i18n";

export default function LanguageSwitcher() {
  const { locale, setLocale } = useLocale();

  return <div
    role="group"
    aria-label={localized(locale, "Language", "Idioma")}
    style={{ display: "flex", padding: 2, border: "1px solid rgba(255,255,255,.1)", borderRadius: 7, background: "rgba(255,255,255,.03)" }}
  >
    {[{ value: "en", label: "EN" }, { value: "pt-BR", label: "PT" }].map(option => (
      <Button
        key={option.value}
        type="button"
        variant="ghost"
        size="sm"
        aria-pressed={locale === option.value}
        onClick={() => setLocale(option.value)}
        style={{ minWidth: 34, height: 25, padding: "0 8px", borderRadius: 5, background: locale === option.value ? "rgba(255,255,255,.12)" : "transparent", color: locale === option.value ? "#f2efec" : "rgba(255,255,255,.45)", fontSize: 10, letterSpacing: ".06em" }}
      >
        {option.label}
      </Button>
    ))}
  </div>;
}
