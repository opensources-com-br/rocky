import { Button } from "@/components/ui/button";
import React from "react";
import CaseCardSummary from "./CaseCardSummary";

export default function CaseGrid({ cases }) {
  return (<>
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "margin": "0 auto", "padding": "40px 28px 64px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"display": "grid", "gridTemplateColumns": "repeat(auto-fill, minmax(280px, 1fr))", "gap": "16px"}} className="responsive-grid">
{cases.map((c, index) => <React.Fragment key={c?.id ?? c?.name ?? index}>
<Button variant="ghost" className="reference-button" type="button" style={{"display": "flex", "flexDirection": "column", "borderRadius": "12px", "overflow": "hidden", "border": "1px solid rgba(255,255,255,.08)", "background": "#0c0a09", "cursor": "pointer"}} onClick={c.open}>
<div style={{"padding": "20px", "borderBottom": "1px solid rgba(255,255,255,.06)", "background": c.tint, "display": "flex", "flexDirection": "column", "gap": "12px"}}>
<div style={{"display": "flex", "alignItems": "center", "gap": "7px"}}>
{c.dots.map((d, index) => <React.Fragment key={d?.id ?? d?.name ?? index}>
<span style={{"width": "7px", "height": "7px", "borderRadius": "50%", "background": d}}>

</span>
</React.Fragment>)}
<span style={{"marginLeft": "auto", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "10.5px", "color": "rgba(255,255,255,.35)"}}>
{c.category}
</span>
</div>
<span style={{"fontSize": "13px", "lineHeight": "1.5", "color": "rgba(255,255,255,.72)", "textWrap": "pretty"}}>
{"“" + c.quote + "”"}
</span>
</div>
<CaseCardSummary c={c} />
</Button>
</React.Fragment>)}
</div>
</div>
</div>
  </>);
}
