import SiteFrame from "@/components/SiteFrame";
import { localized } from "@/lib/i18n";

export default function ShowcaseHeading({ locale }) {
  return (<>
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<SiteFrame className="site-heading">
<div style={{"display": "flex", "alignItems": "center", "gap": "8px", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".12em", "color": "#e08b57"}}>
<span style={{"width": "5px", "height": "5px", "borderRadius": "50%", "background": "#d4703c", "animation": "livepulse 2s ease-in-out infinite"}}>

</span>
{"SHOWCASE "}
</div>
<h1 style={{"margin": "16px 0 12px", "fontSize": "46px", "lineHeight": "1.05", "letterSpacing": "-.04em", "fontWeight": "500", "maxWidth": "640px", "textWrap": "balance"}} className="page-heading">
{localized(locale, "Use cases coming soon", "Casos de uso em breve")}
</h1>
<p style={{"margin": "0", "maxWidth": "560px", "fontSize": "16px", "lineHeight": "1.65", "color": "rgba(255,255,255,.5)", "textWrap": "pretty"}}>
{localized(locale, "You will soon be able to explore Rocky use cases on this page.", "Em breve, você poderá conhecer os casos de uso do Rocky nesta página.")}
</p>
</SiteFrame>
</div>
  </>);
}
