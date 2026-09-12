import RelatedGrid from "./RelatedGrid";

export default function RelatedCases({ related }) {
  return (<>
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "margin": "0 auto", "padding": "44px 28px 56px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11px", "letterSpacing": ".12em", "color": "rgba(255,255,255,.32)"}}>
{"OUTROS CASOS"}
</span>
<RelatedGrid related={related} />
</div>
</div>
  </>);
}
