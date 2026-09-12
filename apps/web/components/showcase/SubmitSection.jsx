import { Button } from "@/components/ui/button";
import SubmitColumns from "./SubmitColumns";

export default function SubmitSection({ backToIndex, copyFile, copyLabel, fields, filename, isSubmit, prSteps, preview, readyLabel }) {
  return (<>
{Boolean(isSubmit) && <>
<div>
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "boxSizing": "border-box", "margin": "0 auto", "padding": "40px 28px 44px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)"}}>
<Button variant="ghost" className="reference-button" type="button" style={{"fontSize": "13px", "color": "rgba(255,255,255,.4)", "cursor": "pointer"}} onClick={backToIndex}>
{"← Showcase"}
</Button>
<h1 style={{"margin": "22px 0 10px", "fontSize": "42px", "lineHeight": "1.05", "letterSpacing": "-.04em", "fontWeight": "500"}} className="page-heading">
{"Enviar o seu depoimento"}
</h1>
<p style={{"margin": "0", "maxWidth": "620px", "fontSize": "16.5px", "lineHeight": "1.6", "color": "rgba(255,255,255,.5)", "textWrap": "pretty"}}>
{"Preencha abaixo e o arquivo do seu caso é montado ao lado, pronto para colar num PR. Nada é enviado para nenhum servidor — a página só formata o texto."}
</p>
</div>
</div>
<SubmitColumns copyFile={copyFile} copyLabel={copyLabel} fields={fields} filename={filename} prSteps={prSteps} preview={preview} readyLabel={readyLabel} />
</div>
</>}
  </>);
}
