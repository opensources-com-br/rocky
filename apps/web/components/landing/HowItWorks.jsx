import SiteFrame from "@/components/SiteFrame";
import { localized } from "@/lib/i18n";
import StepsGrid from "./StepsGrid";

export default function HowItWorks({ locale, steps }) {
  return (<>
<div id={"como"} style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<SiteFrame className="site-section">
<div style={{"display": "flex", "alignItems": "center", "gap": "8px", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".12em", "color": "#e08b57"}}>
<span style={{"width": "5px", "height": "5px", "borderRadius": "50%", "background": "#d4703c", "animation": "livepulse 2s ease-in-out infinite"}}>

</span>
{localized(locale, "HOW IT WORKS ", "COMO FUNCIONA ")}
</div>
<h2 style={{"margin": "16px 0 10px", "fontSize": "38px", "lineHeight": "1.1", "letterSpacing": "-.035em", "fontWeight": "500"}}>
{localized(locale, "Three ways to follow your stream", "Três formas de acompanhar a live")}
</h2>
<p style={{"margin": "0 0 44px", "maxWidth": "480px", "fontSize": "15.5px", "lineHeight": "1.6", "color": "rgba(255,255,255,.45)"}}>
{localized(locale, "A window you can keep on top. Whether it appears on stream depends on the capture sources configured in OBS.", "Uma janela que você pode fixar no topo. A visibilidade na transmissão depende das fontes de captura configuradas no OBS.")}
</p>
<StepsGrid steps={steps} />
</SiteFrame>
</div>
  </>);
}
