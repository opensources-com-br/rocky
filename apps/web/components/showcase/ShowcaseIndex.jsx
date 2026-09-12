import { Button } from "@/components/ui/button";
import React from "react";
import ShowcaseHeading from "./ShowcaseHeading";
import CaseGrid from "./CaseGrid";
import ContributeCallout from "./ContributeCallout";

export default function ShowcaseIndex({ cases, count, filters, isIndex, openSubmit }) {
  return (<>
{Boolean(isIndex) && <>
<div>
<ShowcaseHeading  />
<div style={{"position": "sticky", "top": "56px", "zIndex": "50", "borderBottom": "1px solid rgba(255,255,255,.07)", "background": "rgba(8,7,6,.9)", "backdropFilter": "blur(16px)"}} className="sticky-panel">
<div style={{"maxWidth": "1120px", "margin": "0 auto", "padding": "12px 28px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)", "display": "flex", "flexWrap": "wrap", "alignItems": "center", "gap": "7px"}}>
{filters.map((f, index) => <React.Fragment key={f?.id ?? f?.name ?? index}>
<Button variant="ghost" className="reference-button" type="button" style={{"padding": "6px 12px", "borderRadius": "7px", "cursor": "pointer", "fontSize": "13px", "background": f.bg, "border": "1px solid " + f.border, "color": f.color}} onClick={f.select}>
{f.name}
</Button>
</React.Fragment>)}
<span style={{"marginLeft": "auto", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "12px", "color": "rgba(255,255,255,.32)"}}>
{count}
</span>
</div>
</div>
<CaseGrid cases={cases} />
<ContributeCallout openSubmit={openSubmit} />
</div>
</>}
  </>);
}
