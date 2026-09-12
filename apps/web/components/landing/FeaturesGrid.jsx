import { Badge } from "@/components/ui/badge";
import React from "react";

export default function FeaturesGrid({ features }) {
  return (<>
<div style={{"display": "grid", "gridTemplateColumns": "repeat(auto-fit, minmax(280px, 1fr))", "gap": "1px", "background": "rgba(255,255,255,.07)", "border": "1px solid rgba(255,255,255,.07)", "borderRadius": "12px", "overflow": "hidden"}} className="responsive-grid">
{features.map((f, index) => <React.Fragment key={f?.id ?? f?.name ?? index}>
<div style={{"padding": "26px 24px", "background": "#0c0a09", "display": "flex", "flexDirection": "column", "gap": "9px"}}>
<Badge style={{"alignSelf": "flex-start", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "10px", "letterSpacing": ".1em", "padding": "3px 8px", "borderRadius": "5px", "background": "rgba(212,112,60,.12)", "color": "#e08b57"}}>
{f.tag}
</Badge>
<span style={{"fontSize": "16px", "fontWeight": "500", "letterSpacing": "-.02em"}}>
{f.title}
</span>
<span style={{"fontSize": "14px", "lineHeight": "1.6", "color": "rgba(255,255,255,.45)", "textWrap": "pretty"}}>
{f.text}
</span>
</div>
</React.Fragment>)}
</div>
  </>);
}
