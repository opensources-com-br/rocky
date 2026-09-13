"use client";

import { useLocale } from "@/components/LocaleProvider";
import { getLandingContent } from "@/content/landing";
import LandingView from "./LandingView";

export default function Landing() {
  const { locale } = useLocale();
  return <LandingView {...getLandingContent(locale)} locale={locale} />;
}
