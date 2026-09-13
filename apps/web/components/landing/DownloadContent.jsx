import SiteFrame from "@/components/SiteFrame";
import DownloadButton from "@/components/DownloadButton";
import { localized } from "@/lib/i18n";

export default function DownloadContent({ locale }) {
  return (<>
<SiteFrame className="site-section" style={{"position": "relative", "textAlign": "center", "display": "flex", "flexDirection": "column", "alignItems": "center", "gap": "20px"}}>
<h2 style={{"margin": "0", "maxWidth": "620px", "fontSize": "44px", "lineHeight": "1.07", "letterSpacing": "-.04em", "fontWeight": "500", "textWrap": "balance"}}>
{localized(locale, "Give chat questions more attention.", "Dê mais atenção às perguntas do chat.")}
</h2>
<p style={{"margin": "0", "maxWidth": "460px", "fontSize": "15px", "lineHeight": "1.6", "color": "rgba(255,255,255,.45)"}}>
{localized(locale, "Try the alpha for macOS or Windows. Connect Twitch, Kick, YouTube, or Facebook and test AI before your stream. Unsigned development installers.", "Experimente a versão alpha para macOS ou Windows. Conecte Twitch, Kick, YouTube ou Facebook e teste a IA antes da live. Instaladores de desenvolvimento não assinados.")}
</p>
<div style={{"display": "flex", "flexWrap": "wrap", "justifyContent": "center", "gap": "9px"}}>
<DownloadButton locale={locale} style={{"padding": "12px 24px", "borderRadius": "9px", "background": "#f2efec", "color": "#0d0b0a", "fontSize": "14px", "fontWeight": "500"}} />
<a href={"https://github.com/opensources-com-br/rocky"} style={{"padding": "12px 24px", "borderRadius": "9px", "border": "1px solid rgba(255,255,255,.12)", "background": "rgba(255,255,255,.02)", "color": "rgba(255,255,255,.82)", "fontSize": "14px"}}>
{localized(locale, "View the code", "Ver o código")}
</a>
</div>
</SiteFrame>
  </>);
}
