import ArticleRichBlocks from "./ArticleRichBlocks";
import React from "react";
import ArticleSteps from "./ArticleSteps";
import ArticleTable from "./ArticleTable";

export default function ArticleBlocks({ blocks }) {
  return (<>
{blocks.map((b, index) => <React.Fragment key={b?.id ?? b?.name ?? index}>
<div style={{"marginBottom": "32px"}}>
{Boolean(b.isH2) && <>
<h2 id={b.slug} style={{"margin": "0", "fontSize": "22px", "letterSpacing": "-.03em", "fontWeight": "500", "scrollMarginTop": "80px"}}>
{b.text}
</h2>
</>}
{Boolean(b.isPara) && <>
<p style={{"margin": "0", "fontSize": "15px", "lineHeight": "1.68", "color": "rgba(255,255,255,.55)", "maxWidth": "68ch", "textWrap": "pretty"}}>
{b.text}
</p>
</>}
{Boolean(b.isNote) && <>
<div style={{"display": "flex", "gap": "12px", "padding": "15px 16px", "borderRadius": "10px", "background": "rgba(212,112,60,.07)", "border": "1px solid rgba(212,112,60,.22)", "maxWidth": "68ch"}}>
<span style={{"color": "#e08b57", "fontSize": "14px"}}>
{"ⓘ"}
</span>
<span style={{"fontSize": "14px", "lineHeight": "1.6", "color": "rgba(255,255,255,.62)", "textWrap": "pretty"}}>
{b.text}
</span>
</div>
</>}
<ArticleRichBlocks b={b} />
<ArticleSteps b={b} />
<ArticleTable b={b} />
{Boolean(b.isCards) && <>
<div style={{"display": "grid", "gridTemplateColumns": "repeat(auto-fit, minmax(220px, 1fr))", "gap": "1px", "background": "rgba(255,255,255,.07)", "border": "1px solid rgba(255,255,255,.07)", "borderRadius": "10px", "overflow": "hidden"}} className="responsive-grid">
{b.items.map((cd, index) => <React.Fragment key={cd?.id ?? cd?.name ?? index}>
<div style={{"padding": "18px", "background": "#0a0908", "display": "flex", "flexDirection": "column", "gap": "7px"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "12px", "color": "#e08b57"}}>
{cd.key}
</span>
<span style={{"fontSize": "13.5px", "lineHeight": "1.55", "color": "rgba(255,255,255,.5)", "textWrap": "pretty"}}>
{cd.text}
</span>
</div>
</React.Fragment>)}
</div>
</>}
{Boolean(b.isKeys) && <>
<div style={{"display": "flex", "flexDirection": "column", "gap": "1px", "background": "rgba(255,255,255,.07)", "border": "1px solid rgba(255,255,255,.07)", "borderRadius": "10px", "overflow": "hidden", "maxWidth": "68ch"}}>
{b.items.map((k, index) => <React.Fragment key={k?.id ?? k?.name ?? index}>
<div style={{"display": "flex", "alignItems": "center", "gap": "16px", "padding": "12px 16px", "background": "#0a0908"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "12.5px", "padding": "4px 9px", "borderRadius": "6px", "border": "1px solid rgba(255,255,255,.1)", "background": "rgba(255,255,255,.04)", "color": "rgba(255,255,255,.8)", "minWidth": "92px", "textAlign": "center", "flex": "none"}}>
{k.keys}
</span>
<span style={{"fontSize": "13.5px", "color": "rgba(255,255,255,.55)", "textWrap": "pretty"}}>
{k.text}
</span>
</div>
</React.Fragment>)}
</div>
</>}
</div>
</React.Fragment>)}
  </>);
}
