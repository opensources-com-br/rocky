import React from "react";
import AppTitlebar from "./AppTitlebar";
import AppAnswer from "./AppAnswer";
import AppVoiceFooter from "./AppVoiceFooter";

export default function AppPreview({ chord, heroChips, heroConvo, heroTabs, mic }) {
  return (<>
<div style={{"position": "relative", "marginTop": "60px", "width": "452px", "maxWidth": "100%", "textAlign": "left", "borderRadius": "16px", "overflow": "hidden", "background": "#16100c", "border": "1px solid rgba(255,255,255,.09)", "boxShadow": "0 -12px 80px rgba(212,112,60,.12), 0 40px 90px rgba(0,0,0,.75)"}}>
<AppTitlebar  />
<div style={{"display": "flex", "gap": "7px", "padding": "11px 15px", "borderBottom": "1px solid rgba(255,255,255,.06)"}}>
{heroChips.map((c, index) => <React.Fragment key={c?.id ?? c?.name ?? index}>
<div style={{"display": "flex", "alignItems": "center", "gap": "6px", "padding": "5px 10px 5px 8px", "borderRadius": "999px", "background": "rgba(255,255,255,.04)", "border": "1px solid rgba(255,255,255,.07)"}}>
<span style={{"width": "6px", "height": "6px", "borderRadius": "50%", "background": c.dot}}>

</span>
<span style={{"fontSize": "11.5px", "color": c.text}}>
{c.name}
</span>
</div>
</React.Fragment>)}
</div>
<AppAnswer chord={chord} />
<div style={{"display": "flex", "gap": "3px", "padding": "3px", "margin": "0 15px", "background": "rgba(255,255,255,.04)", "borderRadius": "9px"}}>
{heroTabs.map((t, index) => <React.Fragment key={t?.id ?? t?.name ?? index}>
<span style={{"flex": "1", "textAlign": "center", "padding": "7px 0", "borderRadius": "6px", "fontSize": "11.5px", "background": t.bg, "color": t.color, "fontWeight": t.weight}}>
{t.name}
</span>
</React.Fragment>)}
</div>
<div style={{"padding": "15px", "display": "flex", "flexDirection": "column", "gap": "12px"}}>
{heroConvo.map((c, index) => <React.Fragment key={c?.id ?? c?.name ?? index}>
<div style={{"display": "flex", "gap": "11px"}}>
<span style={{"minWidth": "46px", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "10px", "letterSpacing": ".08em", "paddingTop": "3px", "color": c.color}}>
{c.who}
</span>
<span style={{"fontSize": "13px", "lineHeight": "1.5", "color": "rgba(255,255,255,.8)", "textWrap": "pretty"}}>
{c.text}
</span>
</div>
</React.Fragment>)}
</div>
<AppVoiceFooter mic={mic} />
</div>
  </>);
}
