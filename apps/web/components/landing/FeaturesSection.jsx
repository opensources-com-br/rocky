import FeaturesGrid from "./FeaturesGrid";

export default function FeaturesSection({ features }) {
  return (<>
<div id={"recursos"} style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "margin": "0 auto", "padding": "76px 28px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"display": "flex", "alignItems": "center", "gap": "8px", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".12em", "color": "#e08b57"}}>
<span style={{"width": "5px", "height": "5px", "borderRadius": "50%", "background": "#d4703c"}}>

</span>
{"RECURSOS "}
</div>
<h2 style={{"margin": "16px 0 44px", "fontSize": "38px", "lineHeight": "1.1", "letterSpacing": "-.035em", "fontWeight": "500"}}>
{"O que ele faz enquanto você transmite"}
</h2>
<FeaturesGrid features={features} />
</div>
</div>
  </>);
}
