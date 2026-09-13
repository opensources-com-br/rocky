import SiteFrame from "@/components/SiteFrame";
import { Button } from "@/components/ui/button";
import React from "react";
import DocsArticle from "./DocsArticle";

export default function DocsColumns({ blocks, goNext, goPrev, hasNext, hasPrev, nav, nextTitle, pageGroup, pageLead, pageTitle, prevTitle, toc }) {
  return (<>
<SiteFrame className="docs-columns">
<div style={{"position": "absolute", "top": "0", "bottom": "0", "left": "214px", "width": "1px", "background": "rgba(255,255,255,.07)", "pointerEvents": "none"}}>

</div>
<div style={{"position": "absolute", "top": "0", "bottom": "0", "right": "200px", "width": "1px", "background": "rgba(255,255,255,.07)", "pointerEvents": "none"}}>

</div>
<div style={{"position": "sticky", "top": "var(--site-nav-height)", "padding": "var(--site-heading-space) 18px var(--site-section-space)", "maxHeight": "calc(100dvh - var(--site-nav-height))", "overflowY": "auto"}} className="sticky-panel">
{nav.map((group, index) => <React.Fragment key={group?.id ?? group?.name ?? index}>
<div style={{"display": "flex", "flexDirection": "column", "gap": "2px", "marginBottom": "22px"}}>
<span style={{"padding": "0 10px 8px", "fontSize": "12.5px", "fontWeight": "600", "color": "rgba(255,255,255,.85)"}}>
{group.title}
</span>
{group.items.map((item, index) => <React.Fragment key={item?.id ?? item?.name ?? index}>
<Button variant="ghost" className="reference-button" type="button" style={{"padding": "6px 10px", "borderRadius": "6px", "cursor": "pointer", "fontSize": "13.5px", "color": item.color, "background": item.bg}} onClick={item.go}>
{item.name}
</Button>
</React.Fragment>)}
</div>
</React.Fragment>)}
</div>
<DocsArticle blocks={blocks} goNext={goNext} goPrev={goPrev} hasNext={hasNext} hasPrev={hasPrev} nextTitle={nextTitle} pageGroup={pageGroup} pageLead={pageLead} pageTitle={pageTitle} prevTitle={prevTitle} />
<div style={{"position": "sticky", "top": "var(--site-nav-height)", "padding": "var(--site-heading-space) 20px var(--site-section-space)", "boxSizing": "border-box", "display": "flex", "flexDirection": "column", "gap": "10px"}} className="sticky-panel">
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".1em", "color": "rgba(255,255,255,.35)"}}>
{"NESTA PÁGINA"}
</span>
{toc.map((t, index) => <React.Fragment key={t?.id ?? t?.name ?? index}>
<a href={t.href} style={{"fontSize": "13px", "lineHeight": "1.4", "color": "rgba(255,255,255,.45)"}}>
{t.name}
</a>
</React.Fragment>)}
<a href={"https://github.com/opensources-com-br/rocky"} style={{"marginTop": "14px", "paddingTop": "14px", "borderTop": "1px solid rgba(255,255,255,.07)", "fontSize": "13px", "color": "rgba(255,255,255,.4)"}}>
{"Ver o repositório no GitHub"}
</a>
</div>
</SiteFrame>
  </>);
}
