import AppPreview from "./AppPreview";

export default function HeroContent() {
  return (<>
<div style={{"position": "relative", "maxWidth": "1120px", "margin": "0 auto", "padding": "88px 28px 0", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)", "display": "flex", "flexDirection": "column", "alignItems": "center", "gap": "20px", "textAlign": "center"}}>
<a href={"#recursos"} style={{"display": "inline-flex", "alignItems": "center", "gap": "8px", "padding": "5px 12px 5px 8px", "borderRadius": "999px", "border": "1px solid rgba(255,255,255,.1)", "background": "rgba(255,255,255,.03)", "fontSize": "12.5px", "color": "rgba(255,255,255,.62)"}}>
<span style={{"padding": "1px 7px", "borderRadius": "999px", "background": "rgba(212,112,60,.16)", "color": "#e08b57", "fontSize": "11px", "fontWeight": "500"}}>
{"Open source"}
</span>
{" App gratuito, sem conta Rocky "}
<span style={{"color": "rgba(255,255,255,.35)"}}>
{"→"}
</span>
</a>
<h1 style={{"margin": "0", "maxWidth": "780px", "fontSize": "62px", "lineHeight": "1.02", "letterSpacing": "-.04em", "fontWeight": "500", "textWrap": "balance"}} className="page-heading">
{"O chat ganhou voz."}
<br />
{"E ela fala com você."}
</h1>
<p style={{"margin": "0", "maxWidth": "540px", "fontSize": "16.5px", "lineHeight": "1.6", "color": "rgba(255,255,255,.5)", "textWrap": "pretty"}}>
{"Rocky acompanha Twitch ou Kick, agrupa perguntas e ajuda você a consultar o chat por texto ou voz. Salve notas e ideias usando Ollama local ou sua chave de OpenAI/OpenRouter."}
</p>
<div style={{"display": "flex", "flexWrap": "wrap", "justifyContent": "center", "gap": "9px", "marginTop": "4px"}}>
<a href={"https://github.com/opensources-com-br/rocky/releases"} style={{"padding": "11px 20px", "borderRadius": "9px", "background": "#f2efec", "color": "#0d0b0a", "fontSize": "14px", "fontWeight": "500"}}>
{"Ver downloads alpha"}
</a>
<a href={"https://github.com/opensources-com-br/rocky"} style={{"padding": "11px 20px", "borderRadius": "9px", "border": "1px solid rgba(255,255,255,.12)", "background": "rgba(255,255,255,.02)", "color": "rgba(255,255,255,.82)", "fontSize": "14px"}}>
{"Ver no GitHub"}
</a>
</div>
<span style={{"fontSize": "12px", "color": "rgba(255,255,255,.3)", "fontFamily": "var(--font-geist-mono), monospace"}}>
{"macOS e Windows · alpha · instaladores não assinados"}
</span>
<AppPreview />
<div style={{"height": "88px"}}>

</div>
</div>
  </>);
}
