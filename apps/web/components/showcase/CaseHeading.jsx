import { Button } from "@/components/ui/button";
import React from "react";

export default function CaseHeading({ backToIndex, d }) {
  return (<>
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "margin": "0 auto", "padding": "40px 28px 44px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)"}}>
<Button variant="ghost" className="reference-button" type="button" style={{"fontSize": "13px", "color": "rgba(255,255,255,.4)", "cursor": "pointer"}} onClick={backToIndex}>
{"← Showcase"}
</Button>
<div style={{"display": "flex", "flexWrap": "wrap", "alignItems": "center", "gap": "10px", "marginTop": "22px"}}>
{d.dots.map((dot, index) => <React.Fragment key={dot?.id ?? dot?.name ?? index}>
<span style={{"width": "8px", "height": "8px", "borderRadius": "50%", "background": dot}}>

</span>
</React.Fragment>)}
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".1em", "color": "rgba(255,255,255,.38)"}}>
{d.category}
</span>
</div>
<h1 style={{"margin": "14px 0 10px", "fontSize": "42px", "lineHeight": "1.05", "letterSpacing": "-.04em", "fontWeight": "500"}} className="page-heading">
{d.name}
</h1>
<p style={{"margin": "0 0 16px", "maxWidth": "620px", "fontSize": "16.5px", "lineHeight": "1.6", "color": "rgba(255,255,255,.5)", "textWrap": "pretty"}}>
{d.desc}
</p>
<div style={{"display": "flex", "flexWrap": "wrap", "gap": "8px"}}>
{d.links.map((l, index) => <React.Fragment key={l?.id ?? l?.name ?? index}>
<a href={l.url} style={{"display": "flex", "alignItems": "center", "gap": "7px", "padding": "6px 12px 6px 10px", "borderRadius": "7px", "border": "1px solid rgba(255,255,255,.09)", "background": "rgba(255,255,255,.03)", "fontSize": "12.5px", "color": "rgba(255,255,255,.7)"}}>
<span style={{"width": "7px", "height": "7px", "borderRadius": "50%", "background": l.dot}}>

</span>
{l.label + " "}
</a>
</React.Fragment>)}
</div>
</div>
</div>
  </>);
}
