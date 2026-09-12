import { Button } from "@/components/ui/button";
import Link from "next/link";

export default function ContributeCallout({ openSubmit }) {
  return (<>
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "margin": "0 auto", "padding": "72px 28px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)", "textAlign": "center", "display": "flex", "flexDirection": "column", "alignItems": "center", "gap": "18px"}}>
<h2 style={{"margin": "0", "maxWidth": "520px", "fontSize": "34px", "lineHeight": "1.1", "letterSpacing": "-.04em", "fontWeight": "500", "textWrap": "balance"}}>
{"Roda o Rocky na sua live?"}
</h2>
<p style={{"margin": "0", "maxWidth": "440px", "fontSize": "15px", "lineHeight": "1.6", "color": "rgba(255,255,255,.45)"}}>
{"Prepare um relato real e proponha uma contribuição no GitHub. A publicação depende de revisão."}
</p>
<div style={{"display": "flex", "flexWrap": "wrap", "justifyContent": "center", "gap": "9px"}}>
<Button variant="ghost" className="reference-button" type="button" style={{"padding": "11px 22px", "borderRadius": "9px", "background": "#f2efec", "color": "#0d0b0a", "fontSize": "14px", "fontWeight": "500", "cursor": "pointer"}} onClick={openSubmit}>
{"Enviar o seu"}
</Button>
<Link href={"/docs/"} style={{"padding": "11px 22px", "borderRadius": "9px", "border": "1px solid rgba(255,255,255,.12)", "background": "rgba(255,255,255,.02)", "color": "rgba(255,255,255,.82)", "fontSize": "14px"}}>
{"Ler a documentação"}
</Link>
</div>
</div>
</div>
  </>);
}
