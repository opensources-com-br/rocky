
export default function AppVoiceFooter({ mic }) {
  return (<>
<div style={{"display": "flex", "alignItems": "center", "gap": "11px", "padding": "13px 15px", "borderTop": "1px solid rgba(255,255,255,.06)", "background": "rgba(212,112,60,.06)"}}>
<span>
{mic}
</span>
<div style={{"display": "flex", "flexDirection": "column"}}>
<span style={{"fontSize": "12px", "color": "rgba(255,255,255,.85)"}}>
{"Fale com o Rocky"}
</span>
<span style={{"fontSize": "10.5px", "color": "rgba(255,255,255,.38)"}}>
{"segure ⌥ Espaço"}
</span>
</div>
<span style={{"marginLeft": "auto", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "color": "rgba(255,255,255,.45)"}}>
{"1.221 assistindo"}
</span>
</div>
  </>);
}
