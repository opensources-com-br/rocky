import Link from "next/link";
import { Button } from "@/components/ui/button";

export default function SiteNav({ active, onShowcase }) {
  return <header className="site-nav" style={{ position: "sticky", top: 0, zIndex: 60, borderBottom: "1px solid rgba(255,255,255,.07)", background: "rgba(8,7,6,.8)", backdropFilter: "blur(16px)" }}>
    <nav aria-label="Navegação principal" style={{ maxWidth: 1120, margin: "0 auto", display: "flex", alignItems: "center", gap: 10, padding: "0 28px", height: 56, borderLeft: "1px solid rgba(255,255,255,.07)", borderRight: "1px solid rgba(255,255,255,.07)" }}>
      <Link href="/" style={{ display: "flex", alignItems: "center", gap: 10, color: "inherit", flex: "none" }}>
        <span style={{ width: 22, height: 22, borderRadius: 6, background: "#d4703c", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 11, fontWeight: 700, color: "#140a05" }}>R</span>
        <span style={{ fontSize: 13, fontWeight: 600, letterSpacing: "-.01em" }}>Rocky</span>
      </Link>
      <div style={{ marginLeft: 26, display: "flex", gap: 20, fontSize: 13, color: "rgba(255,255,255,.5)" }}>
        {onShowcase ? <Button variant="ghost" className="reference-button" onClick={onShowcase} style={{ color: "#f2efec" }}>Showcase</Button> : <Link href="/showcase/" style={{ color: "inherit" }}>Showcase</Link>}
        <Link href="/docs/" aria-current={active === "docs" ? "page" : undefined} style={{ color: active === "docs" ? "#f2efec" : "inherit" }}>Docs</Link>
      </div>
      <div style={{ marginLeft: "auto", display: "flex", alignItems: "center", gap: 14, flex: "none" }}>
        <a href="https://github.com/opensources-com-br/rocky" style={{ display: "flex", alignItems: "center", gap: 7, fontSize: 13, color: "rgba(255,255,255,.55)" }}><svg width="14" height="14" viewBox="0 0 16 16" fill="currentColor" aria-hidden="true"><path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82a7.4 7.4 0 0 1 2-.27c.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.01 8.01 0 0 0 16 8c0-4.42-3.58-8-8-8Z" /></svg>GitHub</a>
        <Button asChild className="reference-button" style={{ padding: "7px 14px", borderRadius: 7, background: "#f2efec", color: "#0d0b0a", fontSize: 13, fontWeight: 500 }}><a href="https://github.com/opensources-com-br/rocky/releases">Baixar para Mac</a></Button>
      </div>
    </nav>
  </header>;
}
