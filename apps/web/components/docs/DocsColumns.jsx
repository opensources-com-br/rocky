import SiteFrame from "@/components/SiteFrame";
import DocsArticle from "./DocsArticle";
import DocsSidebar from "./DocsSidebar";

export default function DocsColumns({ blocks, goNext, goPrev, hasNext, hasPrev, locale, nav, nextTitle, pageGroup, pageLead, pageTitle, prevTitle, toc }) {
  return (<>
<SiteFrame className="docs-columns">
<DocsSidebar nav={nav} pageTitle={pageTitle} />
<DocsArticle blocks={blocks} goNext={goNext} goPrev={goPrev} hasNext={hasNext} hasPrev={hasPrev} locale={locale} nextTitle={nextTitle} pageGroup={pageGroup} pageLead={pageLead} pageTitle={pageTitle} prevTitle={prevTitle} />
<div style={{"position": "sticky", "top": "var(--site-nav-height)", "padding": "var(--site-heading-space) 20px var(--site-section-space)", "boxSizing": "border-box", "display": "flex", "flexDirection": "column", "gap": "10px"}} className="sticky-panel">
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".1em", "color": "rgba(255,255,255,.35)"}}>
{localized(locale, "ON THIS PAGE", "NESTA PÁGINA")}
</span>
{toc.map((t, index) => <React.Fragment key={t?.id ?? t?.name ?? index}>
<a href={t.href} style={{"fontSize": "13px", "lineHeight": "1.4", "color": "rgba(255,255,255,.45)"}}>
{t.name}
</a>
</React.Fragment>)}
<a href={"https://github.com/opensources-com-br/rocky"} target="_blank" rel="noreferrer" style={{"marginTop": "14px", "paddingTop": "14px", "borderTop": "1px solid rgba(255,255,255,.07)", "fontSize": "13px", "color": "rgba(255,255,255,.4)"}}>
{localized(locale, "View the repository on GitHub", "Ver o repositório no GitHub")}
</a>
</div>
</SiteFrame>
  </>);
}
