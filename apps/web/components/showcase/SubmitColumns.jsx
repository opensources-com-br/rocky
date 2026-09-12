import ContributionForm from "./ContributionForm";
import ContributionPreview from "./ContributionPreview";

export default function SubmitColumns({ copyFile, copyLabel, fields, filename, prSteps, preview, readyLabel }) {
  return (<>
<div style={{"borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"maxWidth": "1120px", "boxSizing": "border-box", "margin": "0 auto", "padding": "44px 28px 56px", "borderLeft": "1px solid rgba(255,255,255,.07)", "borderRight": "1px solid rgba(255,255,255,.07)", "display": "grid", "gridTemplateColumns": "minmax(0, 1.15fr) minmax(0, 1fr)", "gap": "44px", "alignItems": "start"}} className="responsive-grid">
<ContributionForm copyFile={copyFile} copyLabel={copyLabel} fields={fields} readyLabel={readyLabel} />
<ContributionPreview filename={filename} prSteps={prSteps} preview={preview} />
</div>
</div>
  </>);
}
