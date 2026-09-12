import React from "react";

export default function CaseCardSummary({ c }) {
  return (<>
<div style={{"padding": "16px 20px 18px", "display": "flex", "flexDirection": "column", "gap": "8px"}}>
<span style={{"fontSize": "15.5px", "fontWeight": "500", "letterSpacing": "-.02em"}}>
{c.name}
</span>
<span style={{"fontSize": "13px", "lineHeight": "1.5", "color": "rgba(255,255,255,.42)", "textWrap": "pretty"}}>
{c.desc}
</span>
<div style={{"display": "flex", "flexWrap": "wrap", "gap": "5px", "marginTop": "4px"}}>
{c.stack.map((s, index) => <React.Fragment key={s?.id ?? s?.name ?? index}>
<span style={{"padding": "3px 8px", "borderRadius": "5px", "border": "1px solid rgba(255,255,255,.08)", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "10.5px", "color": "rgba(255,255,255,.45)"}}>
{s}
</span>
</React.Fragment>)}
</div>
<span style={{"marginTop": "6px", "fontSize": "12.5px", "color": "#e08b57"}}>
{"Ler o depoimento →"}
</span>
</div>
  </>);
}
