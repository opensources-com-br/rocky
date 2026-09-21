"use client";
import { ChevronDown } from "lucide-react";
import { useState } from "react";

export default function DocsSidebar({ locale, nav, pageTitle }) {
  const [open, setOpen] = useState(false);
  return <aside className="docs-sidebar sticky-panel" data-open={open}>
    <button className="docs-menu-toggle" type="button" aria-expanded={open} onClick={() => setOpen(!open)}>
      <span>Menu</span><strong>{pageTitle}</strong><ChevronDown size={16} aria-hidden="true" />
    </button>
    <nav className="docs-sidebar-list" aria-label={locale === "pt-BR" ? "Documentação" : "Documentation"}>
      {nav.map(group => <section className="docs-nav-group" key={group.title}>
        <h2>{group.title}</h2>
        {group.items.map(item => <button className="docs-nav-item" type="button" aria-current={item.selected ? "page" : undefined}
          key={item.id} onClick={() => { item.go(); setOpen(false); }}>{item.name}</button>)}
      </section>)}
    </nav>
  </aside>;
}
