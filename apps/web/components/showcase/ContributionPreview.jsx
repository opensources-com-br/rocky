import PullRequestSteps from "./PullRequestSteps";

export default function ContributionPreview({ filename, prSteps, preview }) {
  return (<>
<div style={{"display": "flex", "flexDirection": "column", "gap": "18px", "minWidth": "0", "position": "sticky", "top": "76px"}} className="sticky-panel">
<div style={{"borderRadius": "12px", "border": "1px solid rgba(255,255,255,.09)", "overflow": "hidden"}}>
<div style={{"display": "flex", "alignItems": "center", "gap": "10px", "padding": "11px 15px", "borderBottom": "1px solid rgba(255,255,255,.07)", "background": "#0c0a09"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11.5px", "color": "rgba(255,255,255,.42)"}}>
{filename}
</span>
<span style={{"marginLeft": "auto", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "10.5px", "color": "rgba(255,255,255,.28)"}}>
{"markdown"}
</span>
</div>
<div style={{"padding": "15px 15px 18px", "background": "#0a0908", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "12px", "lineHeight": "1.7", "color": "rgba(255,255,255,.72)", "whiteSpace": "pre-wrap", "wordBreak": "break-word", "maxHeight": "460px", "overflowY": "auto"}}>
{preview}
</div>
</div>
<PullRequestSteps prSteps={prSteps} />
<div style={{"display": "flex", "gap": "11px", "padding": "14px 15px", "borderRadius": "11px", "background": "rgba(212,112,60,.07)", "border": "1px solid rgba(212,112,60,.22)"}}>
<span style={{"color": "#e08b57", "fontSize": "13px"}}>
{"ⓘ"}
</span>
<span style={{"fontSize": "12.5px", "lineHeight": "1.55", "color": "rgba(255,255,255,.58)", "textWrap": "pretty"}}>
{"Não publicamos chave de API, token nem endereço de rede. Se o seu config tiver algo assim, tire antes de enviar."}
</span>
</div>
</div>
  </>);
}
