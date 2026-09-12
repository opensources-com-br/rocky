import React from "react";

export default function StepsGrid({ steps }) {
  return (<>
<div style={{"display": "grid", "gridTemplateColumns": "repeat(auto-fit, minmax(260px, 1fr))", "gap": "1px", "background": "rgba(255,255,255,.07)", "border": "1px solid rgba(255,255,255,.07)", "borderRadius": "12px", "overflow": "hidden"}} className="responsive-grid">
{steps.map((s, index) => <React.Fragment key={s?.id ?? s?.name ?? index}>
<div style={{"padding": "26px 24px", "background": "#0c0a09", "display": "flex", "flexDirection": "column", "gap": "10px"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "color": "rgba(255,255,255,.28)"}}>
{s.n}
</span>
<span style={{"fontSize": "16.5px", "fontWeight": "500", "letterSpacing": "-.02em"}}>
{s.title}
</span>
<span style={{"fontSize": "14px", "lineHeight": "1.6", "color": "rgba(255,255,255,.45)", "textWrap": "pretty"}}>
{s.text}
</span>
</div>
</React.Fragment>)}
</div>
  </>);
}
