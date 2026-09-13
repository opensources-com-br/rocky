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

export function localizedMetadata(locale, pathname) {
  if (pathname.startsWith("/docs")) return {
    title: "Docs · Rocky",
    description: localized(locale, "Rocky installation, configuration, and usage guides.", "Guias de instalação, configuração e uso do Rocky."),
  };
  if (pathname.startsWith("/showcase")) return {
    title: "Showcase · Rocky",
    description: localized(locale, "Rocky use cases are coming soon.", "Casos de uso do Rocky em breve."),
  };
  return {
    title: localized(locale, "Rocky — The chat has a voice", "Rocky — O chat ganhou voz"),
    description: localized(locale, "The voice of chat, in chords. Meet Rocky, read the documentation, and explore the showcase.", "A voz do chat, em acordes. Conheça o Rocky, sua documentação e seu showcase."),
  };
}
