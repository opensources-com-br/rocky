import CaseStory from "./CaseStory";
import CaseConfiguration from "./CaseConfiguration";

export default function CaseBody({ chord, d }) {
  return (<>
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "margin": "0 auto", "padding": "48px 28px 56px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)", "display": "grid", "gridTemplateColumns": "minmax(0, 1.6fr) minmax(0, 1fr)", "gap": "48px", "alignItems": "start"}} className="responsive-grid">
<CaseStory d={d} />
<CaseConfiguration chord={chord} d={d} />
</div>
</div>
  </>);
}
