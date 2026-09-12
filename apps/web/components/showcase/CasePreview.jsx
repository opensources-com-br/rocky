import CasePreviewTitle from "./CasePreviewTitle";

export default function CasePreview({ chord, d }) {
  return (<>
<div style={{"borderRadius": "12px", "overflow": "hidden", "background": "#16100c", "border": "1px solid rgba(255,255,255,.09)"}}>
<CasePreviewTitle  />
<div style={{"padding": "15px", "background": "linear-gradient(180deg, rgba(212,112,60,.1), rgba(212,112,60,0))"}}>
<div style={{"display": "flex", "alignItems": "center", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "9.5px", "letterSpacing": ".14em", "color": "#e08b57"}}>
{" EXEMPLO DE PEDIDO "}
<span style={{"marginLeft": "auto"}}>
{chord}
</span>
</div>
<p style={{"margin": "11px 0 0", "fontSize": "16px", "lineHeight": "1.38", "letterSpacing": "-.025em", "fontWeight": "500", "textWrap": "pretty"}}>
{d.sample}
</p>
</div>
</div>
  </>);
}
