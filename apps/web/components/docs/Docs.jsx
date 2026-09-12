"use client";
import { useState } from "react";
import DocsView from "./DocsView";
import { docsView } from "./docs-view";
export default function Docs() {
  const [page, setPage] = useState("Introdução");
  function go(name) { setPage(name); window.scrollTo({ top: 0 }); }
  return <DocsView {...docsView(page, go)} />;
}
