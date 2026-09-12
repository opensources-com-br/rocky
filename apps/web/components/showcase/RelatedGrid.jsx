import { Button } from "@/components/ui/button";
import React from "react";

export default function RelatedGrid({ related }) {
  return (<>
<div style={{"marginTop": "18px", "display": "grid", "gridTemplateColumns": "repeat(auto-fill, minmax(240px, 1fr))", "gap": "14px"}} className="responsive-grid">
{related.map((r, index) => <React.Fragment key={r?.id ?? r?.name ?? index}>
<Button variant="ghost" className="reference-button" type="button" style={{"padding": "18px", "borderRadius": "11px", "border": "1px solid rgba(255,255,255,.08)", "background": "#0c0a09", "cursor": "pointer", "display": "flex", "flexDirection": "column", "gap": "9px"}} onClick={r.open}>
<div style={{"display": "flex", "alignItems": "center", "gap": "6px"}}>
{r.dots.map((rd, index) => <React.Fragment key={rd?.id ?? rd?.name ?? index}>
<span style={{"width": "6px", "height": "6px", "borderRadius": "50%", "background": rd}}>

</span>
</React.Fragment>)}
<span style={{"marginLeft": "auto", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "10px", "color": "rgba(255,255,255,.3)"}}>
{r.category}
</span>
</div>
<span style={{"fontSize": "14.5px", "fontWeight": "500", "letterSpacing": "-.02em"}}>
{r.name}
</span>
<span style={{"fontSize": "12.5px", "lineHeight": "1.5", "color": "rgba(255,255,255,.4)", "textWrap": "pretty"}}>
{r.desc}
</span>
</Button>
</React.Fragment>)}
</div>
  </>);
}
