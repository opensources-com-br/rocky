import ShowcaseNav from "./ShowcaseNav";
import ShowcaseIndex from "./ShowcaseIndex";
import CaseDetail from "./CaseDetail";
import SubmitSection from "./SubmitSection";
import ShowcaseFooter from "./ShowcaseFooter";

export default function ShowcaseView({ d, backToIndex, cases, chord, copyFile, copyLabel, count, fields, filename, filters, isDetail, isIndex, isSubmit, openSubmit, prSteps, preview, readyLabel, related }) {
  return (<>
<div style={{"background": "#080706", "color": "#f2efec", "fontFamily": "var(--font-geist-sans), sans-serif", "letterSpacing": "-.011em", "minHeight": "100vh"}}>
<ShowcaseNav backToIndex={backToIndex} />
<ShowcaseIndex cases={cases} count={count} filters={filters} isIndex={isIndex} openSubmit={openSubmit} />
<CaseDetail backToIndex={backToIndex} chord={chord} d={d} isDetail={isDetail} related={related} />
<SubmitSection backToIndex={backToIndex} copyFile={copyFile} copyLabel={copyLabel} fields={fields} filename={filename} isSubmit={isSubmit} prSteps={prSteps} preview={preview} readyLabel={readyLabel} />
<ShowcaseFooter  />
</div>
  </>);
}
