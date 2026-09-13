import SiteFrame from "@/components/SiteFrame";
import { localized } from "@/lib/i18n";
import React from "react";

export default function PlatformsStrip({ locale, platforms }) {
  return (<>
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<SiteFrame className="site-strip" style={{"display": "flex", "flexWrap": "wrap", "alignItems": "center", "gap": "14px 36px"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".1em", "color": "rgba(255,255,255,.3)"}}>
{localized(locale, "INTEGRATIONS", "INTEGRAÇÕES")}
</span>
{platforms.map((p, index) => <React.Fragment key={p?.id ?? p?.name ?? index}>
<div style={{"display": "flex", "alignItems": "center", "gap": "8px"}}>
<span style={{"width": "7px", "height": "7px", "borderRadius": "50%", "background": p.color}}>

</span>
<span style={{"fontSize": "14px", "color": "rgba(255,255,255,.55)"}}>
{p.name}
</span>
</div>
</React.Fragment>)}
<span style={{"marginLeft": "auto", "fontSize": "13px", "color": "rgba(255,255,255,.28)"}}>
{localized(locale, "No direct OBS integration", "Sem integração direta com OBS")}
</span>
</SiteFrame>
</div>
  </>);
}
