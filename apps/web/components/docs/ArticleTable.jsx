import React from "react";

export default function ArticleTable({ b }) {
  return (<>
{Boolean(b.isTable) && <>
<div style={{"borderRadius": "10px", "border": "1px solid rgba(255,255,255,.08)", "overflow": "hidden", "maxWidth": "68ch"}}>
<div style={{"display": "grid", "gridTemplateColumns": b.cols, "gap": "12px", "padding": "11px 16px", "background": "#0c0a09", "borderBottom": "1px solid rgba(255,255,255,.07)", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".08em", "color": "rgba(255,255,255,.35)"}} className="responsive-grid">
{b.head.map((hd, index) => <React.Fragment key={hd?.id ?? hd?.name ?? index}>
<span>
{hd}
</span>
</React.Fragment>)}
</div>
{b.rows.map((row, index) => <React.Fragment key={row?.id ?? row?.name ?? index}>
<div style={{"display": "grid", "gridTemplateColumns": b.cols, "gap": "12px", "padding": "13px 16px", "borderBottom": "1px solid rgba(255,255,255,.05)", "fontSize": "13.5px", "color": "rgba(255,255,255,.55)"}} className="responsive-grid">
{row.map((cell, index) => <React.Fragment key={cell?.id ?? cell?.name ?? index}>
<span style={{"minWidth": "0"}}>
{cell}
</span>
</React.Fragment>)}
</div>
</React.Fragment>)}
</div>
</>}
  </>);
}
