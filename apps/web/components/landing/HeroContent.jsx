import SiteFrame from "@/components/SiteFrame";
import DownloadButton from "@/components/DownloadButton";
import { localized } from "@/lib/i18n";
import AppPreview from "./AppPreview";

export default function HeroContent({ locale }) {
  return (<>
<SiteFrame className="site-hero" style={{"position": "relative", "display": "flex", "flexDirection": "column", "alignItems": "center", "gap": "20px", "textAlign": "center"}}>
<a href={"#recursos"} style={{"display": "inline-flex", "alignItems": "center", "gap": "8px", "padding": "5px 12px 5px 8px", "borderRadius": "999px", "border": "1px solid rgba(255,255,255,.1)", "background": "rgba(255,255,255,.03)", "fontSize": "12.5px", "color": "rgba(255,255,255,.62)"}}>
<span style={{"padding": "1px 7px", "borderRadius": "999px", "background": "rgba(212,112,60,.16)", "color": "#e08b57", "fontSize": "11px", "fontWeight": "500"}}>
{"Open source"}
</span>
{localized(locale, " Free app, no Rocky account ", " App gratuito, sem conta Rocky ")}
<span style={{"color": "rgba(255,255,255,.35)"}}>
{"→"}
</span>
</a>
<h1 style={{"margin": "0", "maxWidth": "780px", "fontSize": "62px", "lineHeight": "1.02", "letterSpacing": "-.04em", "fontWeight": "500", "textWrap": "balance"}} className="page-heading">
{localized(locale, "The chat has a voice.", "O chat ganhou voz.")}
<br />
{localized(locale, "And it talks with you.", "E ela fala com você.")}
</h1>
<p style={{"margin": "0", "maxWidth": "540px", "fontSize": "16.5px", "lineHeight": "1.6", "color": "rgba(255,255,255,.5)", "textWrap": "pretty"}}>
{localized(locale, "Rocky follows Twitch, Kick, YouTube, or Facebook, groups questions, and helps you check chat through quick actions or voice. Save notes and ideas using local Ollama or your OpenAI/OpenRouter key.", "Rocky acompanha Twitch, Kick, YouTube ou Facebook, agrupa perguntas e ajuda você a consultar o chat por ações rápidas ou voz. Salve notas e ideias usando Ollama local ou sua chave de OpenAI/OpenRouter.")}
</p>
<div style={{"display": "flex", "flexWrap": "wrap", "justifyContent": "center", "gap": "9px", "marginTop": "4px"}}>
<DownloadButton locale={locale} style={{"padding": "11px 20px", "borderRadius": "9px", "background": "#f2efec", "color": "#0d0b0a", "fontSize": "14px", "fontWeight": "500"}} />
<a href={"https://github.com/opensources-com-br/rocky"} style={{"padding": "11px 20px", "borderRadius": "9px", "border": "1px solid rgba(255,255,255,.12)", "background": "rgba(255,255,255,.02)", "color": "rgba(255,255,255,.82)", "fontSize": "14px"}}>
{localized(locale, "View on GitHub", "Ver no GitHub")}
</a>
</div>
<span style={{"fontSize": "12px", "color": "rgba(255,255,255,.3)", "fontFamily": "var(--font-geist-mono), monospace"}}>
{localized(locale, "macOS and Windows · alpha · unsigned installers", "macOS e Windows · alpha · instaladores não assinados")}
</span>
<AppPreview locale={locale} />
</SiteFrame>
  </>);
}
