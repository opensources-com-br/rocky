import React from "react";
export default function ArticleRichBlocks({ b }) {
return <>
{Boolean(b.isCode) && <>
<div style={{"borderRadius": "10px", "border": "1px solid rgba(255,255,255,.09)", "overflow": "hidden", "maxWidth": "68ch"}}>
<div style={{"padding": "10px 16px", "borderBottom": "1px solid rgba(255,255,255,.07)", "background": "#0c0a09", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "12px", "color": "rgba(255,255,255,.42)"}}>
{b.file}
</div>
<div style={{"padding": "16px 16px 20px", "background": "#0a0908", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "13px", "lineHeight": "1.75", "color": "rgba(255,255,255,.75)", "whiteSpace": "pre", "overflowX": "auto"}}>
{b.text}
</div>
</div>
</>}
{Boolean(b.isList) && <>
<div style={{"display": "flex", "flexDirection": "column", "gap": "10px", "maxWidth": "68ch"}}>
{b.items.map((li, index) => <React.Fragment key={li?.id ?? li?.name ?? index}>
<div style={{"display": "flex", "gap": "11px"}}>
<span style={{"width": "5px", "height": "5px", "borderRadius": "50%", "background": "#d4703c", "marginTop": "9px", "flex": "none"}}>

</span>
<span style={{"fontSize": "15px", "lineHeight": "1.6", "color": "rgba(255,255,255,.55)", "textWrap": "pretty"}}>
{li}
</span>
</div>
</React.Fragment>)}
</div>
</>}
</>;
}
