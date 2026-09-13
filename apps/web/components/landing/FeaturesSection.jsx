import SiteFrame from "@/components/SiteFrame";
import { localized } from "@/lib/i18n";
import FeaturesGrid from "./FeaturesGrid";

export default function FeaturesSection({ features, locale }) {
  return (<>
<div id={"recursos"} style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<SiteFrame className="site-section">
<div style={{"display": "flex", "alignItems": "center", "gap": "8px", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".12em", "color": "#e08b57"}}>
<span style={{"width": "5px", "height": "5px", "borderRadius": "50%", "background": "#d4703c"}}>

</span>
{localized(locale, "FEATURES ", "RECURSOS ")}
</div>
<h2 style={{"margin": "16px 0 44px", "fontSize": "38px", "lineHeight": "1.1", "letterSpacing": "-.035em", "fontWeight": "500"}}>
{localized(locale, "What it does while you stream", "O que ele faz enquanto você transmite")}
</h2>
<FeaturesGrid features={features} />
</SiteFrame>
</div>
  </>);
}
