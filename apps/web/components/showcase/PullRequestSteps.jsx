import React from "react";

export default function PullRequestSteps({ prSteps }) {
  return (<>
<div style={{"borderRadius": "12px", "border": "1px solid rgba(255,255,255,.08)", "overflow": "hidden"}}>
<div style={{"padding": "11px 15px", "borderBottom": "1px solid rgba(255,255,255,.07)", "background": "#0c0a09", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".1em", "color": "rgba(255,255,255,.35)"}}>
{"COMO ABRIR O PR"}
</div>
{prSteps.map((s, index) => <React.Fragment key={s?.id ?? s?.name ?? index}>
<div style={{"display": "flex", "gap": "12px", "padding": "13px 15px", "borderBottom": "1px solid rgba(255,255,255,.05)"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11.5px", "color": "#e08b57", "paddingTop": "2px", "flex": "none"}}>
{s.n}
</span>
<span style={{"fontSize": "13px", "lineHeight": "1.55", "color": "rgba(255,255,255,.55)", "textWrap": "pretty"}}>
{s.text}
</span>
</div>
</React.Fragment>)}
</div>
  </>);
}
