"use client";
import { useState } from "react";
import { useLocale } from "@/components/LocaleProvider";
import DocsView from "./DocsView";
import { docsView } from "./docs-view";
export default function Docs() {
  const { locale } = useLocale();
  const [page, setPage] = useState("introduction");
  function go(name) { setPage(name); window.scrollTo({ top: 0 }); }
  return <DocsView {...docsView(page, go, locale)} />;
}
