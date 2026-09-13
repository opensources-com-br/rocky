export const DEFAULT_LOCALE = "en";
export const PORTUGUESE_LOCALE = "pt-BR";
export const SUPPORTED_LOCALES = [DEFAULT_LOCALE, PORTUGUESE_LOCALE];
export const LOCALE_STORAGE_KEY = "rocky.web.locale";

export function normalizeLocale(value) {
  if (typeof value !== "string") return DEFAULT_LOCALE;
  const normalized = value.trim().toLowerCase();
  return normalized === "pt" || normalized === "pt-br" ? PORTUGUESE_LOCALE : DEFAULT_LOCALE;
}

export function localized(locale, english, portuguese) {
  return locale === PORTUGUESE_LOCALE ? portuguese : english;
}
