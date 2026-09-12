import React from "react";

export default function ArticleSteps({ b }) {
  return (<>
{Boolean(b.isSteps) && <>
<div style={{"display": "flex", "flexDirection": "column", "gap": "1px", "background": "rgba(255,255,255,.07)", "border": "1px solid rgba(255,255,255,.07)", "borderRadius": "10px", "overflow": "hidden", "maxWidth": "68ch"}}>
{b.items.map((st, index) => <React.Fragment key={st?.id ?? st?.name ?? index}>
<div style={{"display": "flex", "gap": "14px", "padding": "16px 18px", "background": "#0a0908"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "12px", "color": "#e08b57", "paddingTop": "2px"}}>
{st.n}
</span>
<div style={{"display": "flex", "flexDirection": "column", "gap": "5px", "minWidth": "0"}}>
<span style={{"fontSize": "14.5px", "fontWeight": "500", "letterSpacing": "-.02em"}}>
{st.title}
</span>
<span style={{"fontSize": "13.5px", "lineHeight": "1.6", "color": "rgba(255,255,255,.45)", "textWrap": "pretty"}}>
{st.text}
</span>
</div>
</div>
</React.Fragment>)}
</div>
</>}
  </>);
}
