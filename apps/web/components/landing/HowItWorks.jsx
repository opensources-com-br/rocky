import StepsGrid from "./StepsGrid";

export default function HowItWorks({ steps }) {
  return (<>
<div id={"como"} style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "margin": "0 auto", "padding": "76px 28px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"display": "flex", "alignItems": "center", "gap": "8px", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".12em", "color": "#e08b57"}}>
<span style={{"width": "5px", "height": "5px", "borderRadius": "50%", "background": "#d4703c", "animation": "livepulse 2s ease-in-out infinite"}}>

</span>
{"COMO FUNCIONA "}
</div>
<h2 style={{"margin": "16px 0 10px", "fontSize": "38px", "lineHeight": "1.1", "letterSpacing": "-.035em", "fontWeight": "500"}}>
{"Três formas de acompanhar a live"}
</h2>
<p style={{"margin": "0 0 44px", "maxWidth": "480px", "fontSize": "15.5px", "lineHeight": "1.6", "color": "rgba(255,255,255,.45)"}}>
{"Uma janela que você pode fixar no topo. A visibilidade na transmissão depende das fontes de captura configuradas no OBS."}
</p>
<StepsGrid steps={steps} />
</div>
</div>
  </>);
}
