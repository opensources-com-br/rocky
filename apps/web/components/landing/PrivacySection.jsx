import SiteFrame from "@/components/SiteFrame";
import { localized } from "@/lib/i18n";
import React from "react";
import ProviderCards from "./ProviderCards";

export default function PrivacySection({ guarantees, localCards, locale }) {
  return (<>
<div id={"local"} style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<SiteFrame className="site-section">
<div style={{"display": "flex", "alignItems": "center", "gap": "8px", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".12em", "color": "#e08b57"}}>
<span style={{"width": "5px", "height": "5px", "borderRadius": "50%", "background": "#d4703c"}}>

</span>
{localized(locale, "LOCAL & PRIVATE ", "LOCAL & PRIVADO ")}
</div>
<h2 style={{"margin": "16px 0 10px", "fontSize": "38px", "lineHeight": "1.1", "letterSpacing": "-.035em", "fontWeight": "500"}}>
{localized(locale, "You choose where AI runs", "Você escolhe onde a IA processa")}
</h2>
<p style={{"margin": "0 0 44px", "maxWidth": "520px", "fontSize": "15.5px", "lineHeight": "1.6", "color": "rgba(255,255,255,.45)"}}>
{localized(locale, "Rocky has no proprietary backend. Records stay on your computer; when you choose remote AI, the request and chat sample are sent to that provider.", "Rocky não tem backend próprio. Registros ficam no computador; ao escolher IA remota, o pedido e a amostra de chat são enviados ao provedor.")}
</p>
<ProviderCards localCards={localCards} />
<div style={{"marginTop": "16px", "display": "grid", "gridTemplateColumns": "repeat(auto-fit, minmax(220px, 1fr))", "gap": "1px", "background": "rgba(255,255,255,.07)", "border": "1px solid rgba(255,255,255,.07)", "borderRadius": "12px", "overflow": "hidden"}} className="responsive-grid">
{guarantees.map((g, index) => <React.Fragment key={g?.id ?? g?.name ?? index}>
<div style={{"padding": "22px 20px", "background": "#0c0a09", "display": "flex", "flexDirection": "column", "gap": "7px"}}>
<span style={{"fontSize": "14.5px", "fontWeight": "500", "letterSpacing": "-.02em"}}>
{g.title}
</span>
<span style={{"fontSize": "13.5px", "lineHeight": "1.55", "color": "rgba(255,255,255,.42)", "textWrap": "pretty"}}>
{g.text}
</span>
</div>
</React.Fragment>)}
</div>
</SiteFrame>
</div>
  </>);
}
