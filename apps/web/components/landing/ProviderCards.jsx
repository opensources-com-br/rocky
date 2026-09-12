import { Badge } from "@/components/ui/badge";
import React from "react";

export default function ProviderCards({ localCards }) {
  return (<>
<div style={{"display": "grid", "gridTemplateColumns": "repeat(auto-fit, minmax(280px, 1fr))", "gap": "16px"}} className="responsive-grid">
{localCards.map((c, index) => <React.Fragment key={c?.id ?? c?.name ?? index}>
<div style={{"padding": "26px 24px", "borderRadius": "12px", "background": "#0c0a09", "border": "1px solid rgba(255,255,255,.08)", "display": "flex", "flexDirection": "column", "gap": "13px"}}>
<Badge style={{"alignSelf": "flex-start", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "10px", "letterSpacing": ".1em", "padding": "3px 8px", "borderRadius": "5px", "background": "rgba(212,112,60,.12)", "color": "#e08b57"}}>
{c.tag}
</Badge>
<span style={{"fontSize": "17px", "fontWeight": "500", "letterSpacing": "-.025em"}}>
{c.title}
</span>
<span style={{"fontSize": "14px", "lineHeight": "1.6", "color": "rgba(255,255,255,.45)", "textWrap": "pretty"}}>
{c.text}
</span>
<div style={{"display": "flex", "flexWrap": "wrap", "gap": "6px", "marginTop": "2px"}}>
{c.chips.map((chip, index) => <React.Fragment key={chip?.id ?? chip?.name ?? index}>
<span style={{"padding": "4px 9px", "borderRadius": "6px", "border": "1px solid rgba(255,255,255,.09)", "background": "rgba(255,255,255,.03)", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "color": "rgba(255,255,255,.55)"}}>
{chip}
</span>
</React.Fragment>)}
</div>
</div>
</React.Fragment>)}
</div>
  </>);
}
