import React from "react";

export default function CaseChanges({ d }) {
  return (<>
<div style={{"paddingTop": "8px", "display": "flex", "flexDirection": "column", "gap": "14px"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".12em", "color": "rgba(255,255,255,.32)"}}>
{"O QUE VOCÊ PODE FAZER"}
</span>
<div style={{"display": "flex", "flexDirection": "column", "gap": "1px", "background": "rgba(255,255,255,.07)", "border": "1px solid rgba(255,255,255,.07)", "borderRadius": "10px", "overflow": "hidden"}}>
{d.changes.map((ch, index) => <React.Fragment key={ch?.id ?? ch?.name ?? index}>
<div style={{"display": "flex", "gap": "12px", "padding": "15px 17px", "background": "#0a0908"}}>
<span style={{"width": "5px", "height": "5px", "borderRadius": "50%", "background": "#d4703c", "marginTop": "8px", "flex": "none"}}>

</span>
<span style={{"fontSize": "14px", "lineHeight": "1.6", "color": "rgba(255,255,255,.58)", "textWrap": "pretty"}}>
{ch}
</span>
</div>
</React.Fragment>)}
</div>
</div>
  </>);
}
