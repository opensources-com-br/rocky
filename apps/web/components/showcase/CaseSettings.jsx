import React from "react";

export default function CaseSettings({ d }) {
  return (<>
<div style={{"borderRadius": "12px", "border": "1px solid rgba(255,255,255,.08)", "overflow": "hidden"}}>
<div style={{"padding": "13px 16px", "borderBottom": "1px solid rgba(255,255,255,.07)", "background": "#0c0a09", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".1em", "color": "rgba(255,255,255,.35)"}}>
{"CONFIGURAÇÃO"}
</div>
{d.setup.map((s, index) => <React.Fragment key={s?.id ?? s?.name ?? index}>
<div style={{"display": "flex", "gap": "12px", "padding": "13px 16px", "borderBottom": "1px solid rgba(255,255,255,.05)"}}>
<span style={{"fontSize": "13px", "color": "rgba(255,255,255,.4)", "minWidth": "74px", "flex": "none"}}>
{s.label}
</span>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "12.5px", "color": "rgba(255,255,255,.8)", "textAlign": "right", "marginLeft": "auto"}}>
{s.value}
</span>
</div>
</React.Fragment>)}
</div>
  </>);
}
