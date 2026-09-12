import { Button } from "@/components/ui/button";

export default function ArticlePagination({ goNext, goPrev, hasNext, hasPrev, nextTitle, prevTitle }) {
  return (<>
<div style={{"display": "flex", "gap": "12px", "paddingTop": "28px", "borderTop": "1px solid rgba(255,255,255,.07)"}}>
{Boolean(hasPrev) && <>
<Button variant="ghost" className="reference-button" type="button" style={{"flex": "1", "padding": "16px 18px", "borderRadius": "10px", "border": "1px solid rgba(255,255,255,.08)", "display": "flex", "flexDirection": "column", "gap": "4px", "cursor": "pointer"}} onClick={goPrev}>
<span style={{"fontSize": "12px", "color": "rgba(255,255,255,.35)"}}>
{"Anterior"}
</span>
<span style={{"fontSize": "14.5px", "color": "rgba(255,255,255,.85)"}}>
{prevTitle}
</span>
</Button>
</>}
{Boolean(hasNext) && <>
<Button variant="ghost" className="reference-button" type="button" style={{"flex": "1", "padding": "16px 18px", "borderRadius": "10px", "border": "1px solid rgba(255,255,255,.08)", "display": "flex", "flexDirection": "column", "gap": "4px", "textAlign": "right", "cursor": "pointer"}} onClick={goNext}>
<span style={{"fontSize": "12px", "color": "rgba(255,255,255,.35)"}}>
{"Próximo"}
</span>
<span style={{"fontSize": "14.5px", "color": "rgba(255,255,255,.85)"}}>
{nextTitle}
</span>
</Button>
</>}
</div>
  </>);
}
