
export default function AppTitlebar() {
  return (<>
<div style={{"display": "flex", "alignItems": "center", "gap": "11px", "padding": "13px 15px", "borderBottom": "1px solid rgba(255,255,255,.06)"}}>
<div style={{"display": "flex", "gap": "7px"}}>
<span style={{"width": "10px", "height": "10px", "borderRadius": "50%", "background": "#ff5f57"}}>

</span>
<span style={{"width": "10px", "height": "10px", "borderRadius": "50%", "background": "#febc2e"}}>

</span>
<span style={{"width": "10px", "height": "10px", "borderRadius": "50%", "background": "#28c840"}}>

</span>
</div>
<div style={{"marginLeft": "5px", "display": "flex", "flexDirection": "column"}}>
<span style={{"fontSize": "13.5px", "fontWeight": "600"}}>
{"Rocky"}
</span>
<span style={{"fontSize": "11px", "color": "rgba(255,255,255,.38)"}}>
{"a voz do chat, em acordes"}
</span>
</div>
<span style={{"marginLeft": "auto", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "10px", "letterSpacing": ".1em", "padding": "4px 9px", "borderRadius": "999px", "color": "#e08b57", "border": "1px solid rgba(212,112,60,.32)", "background": "rgba(212,112,60,.1)"}}>
{"OUVINDO"}
</span>
</div>
  </>);
}
