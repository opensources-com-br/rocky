
export default function AppAnswer({ chord }) {
  return (<>
<div style={{"padding": "18px 16px 16px", "background": "linear-gradient(180deg, rgba(212,112,60,.1), rgba(212,112,60,0))"}}>
<div style={{"display": "flex", "alignItems": "center", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "10px", "letterSpacing": ".14em", "color": "#e08b57"}}>
{" ♪ TOCANDO AGORA "}
<span style={{"marginLeft": "auto"}}>
{chord}
</span>
</div>
<p style={{"margin": "13px 0 0", "fontSize": "21px", "lineHeight": "1.32", "letterSpacing": "-.025em", "fontWeight": "500", "textWrap": "pretty"}}>
{"Sete pessoas perguntaram o preço do curso nos últimos dois minutos. Vale responder agora."}
</p>
<div style={{"display": "flex", "alignItems": "center", "gap": "8px", "marginTop": "12px", "fontSize": "11.5px", "color": "rgba(255,255,255,.42)"}}>
<span style={{"width": "6px", "height": "6px", "borderRadius": "50%", "background": "#9146ff"}}>

</span>
{"4 na Twitch "}
<span style={{"width": "6px", "height": "6px", "borderRadius": "50%", "background": "#ff0033"}}>

</span>
{"2 no YouTube "}
<span style={{"width": "6px", "height": "6px", "borderRadius": "50%", "background": "#53fc18"}}>

</span>
{"1 no Kick "}
</div>
</div>
  </>);
}
