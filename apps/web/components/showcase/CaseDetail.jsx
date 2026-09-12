import React from "react";
import CaseHeading from "./CaseHeading";
import CaseBody from "./CaseBody";
import RelatedCases from "./RelatedCases";

export default function CaseDetail({ backToIndex, chord, d, isDetail, related }) {
  return (<>
{Boolean(isDetail) && <>
<div>
<CaseHeading backToIndex={backToIndex} d={d} />
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "margin": "0 auto", "padding": "0", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)", "display": "grid", "gridTemplateColumns": "repeat(auto-fit, minmax(180px, 1fr))"}} className="responsive-grid">
{d.stats.map((st, index) => <React.Fragment key={st?.id ?? st?.name ?? index}>
<div style={{"padding": "24px 28px", "borderRight": "1px solid rgba(255,255,255,.07)", "display": "flex", "flexDirection": "column", "gap": "6px"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "10.5px", "letterSpacing": ".1em", "color": "rgba(255,255,255,.32)"}}>
{st.label}
</span>
<span style={{"fontSize": "22px", "letterSpacing": "-.03em", "fontWeight": "500"}}>
{st.value}
</span>
</div>
</React.Fragment>)}
</div>
</div>
<CaseBody chord={chord} d={d} />
<RelatedCases related={related} />
</div>
</>}
  </>);
}
