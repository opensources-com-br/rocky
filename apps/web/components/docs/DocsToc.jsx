"use client";
import { ChevronDown } from "lucide-react";
import { useState } from "react";
import { localized } from "@/lib/i18n";

export default function DocsToc({ locale, toc }) {
  const [open, setOpen] = useState(false);
  const label = localized(locale, "On this page", "Nesta página");
  return <aside className="docs-toc sticky-panel" data-open={open}>
    <button className="docs-toc-toggle" type="button" aria-expanded={open} onClick={() => setOpen(!open)}>
      <span>{label}</span><strong>{toc[0]?.name}</strong><ChevronDown size={16} aria-hidden="true" />
    </button>
    <nav className="docs-toc-links" aria-label={label}>
      <span>{label.toUpperCase()}</span>
      {toc.map(item => <a href={item.href} key={item.href} onClick={() => setOpen(false)}>{item.name}</a>)}
      <a className="docs-repository-link" href="https://github.com/opensources-com-br/rocky" target="_blank" rel="noreferrer">
        {localized(locale, "View the repository on GitHub", "Ver o repositório no GitHub")}</a>
    </nav>
  </aside>;
}
