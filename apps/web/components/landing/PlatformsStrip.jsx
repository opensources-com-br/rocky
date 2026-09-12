import React from "react";

export default function PlatformsStrip({ platforms }) {
  return (<>
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "margin": "0 auto", "padding": "26px 28px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)", "display": "flex", "flexWrap": "wrap", "alignItems": "center", "gap": "14px 36px"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".1em", "color": "rgba(255,255,255,.3)"}}>
{"FUNCIONA COM"}
</span>
{platforms.map((p, index) => <React.Fragment key={p?.id ?? p?.name ?? index}>
<div style={{"display": "flex", "alignItems": "center", "gap": "8px"}}>
<span style={{"width": "7px", "height": "7px", "borderRadius": "50%", "background": p.color}}>

</span>
<span style={{"fontSize": "14px", "color": "rgba(255,255,255,.55)"}}>
{p.name}
</span>
</div>
</React.Fragment>)}
<span style={{"marginLeft": "auto", "fontSize": "13px", "color": "rgba(255,255,255,.28)"}}>
{"OBS · Streamlabs · Restream"}
</span>
</div>
</div>
  </>);
}
