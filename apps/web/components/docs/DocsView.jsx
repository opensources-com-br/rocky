import DocsNav from "./DocsNav";
import DocsColumns from "./DocsColumns";

export default function DocsView({ blocks, goNext, goPrev, hasNext, hasPrev, nav, nextTitle, pageGroup, pageLead, pageTitle, prevTitle, toc }) {
  return (<>
<div style={{"background": "#080706", "color": "#f2efec", "fontFamily": "var(--font-geist-sans), sans-serif", "letterSpacing": "-.011em", "minHeight": "100vh"}}>
<DocsNav  />
<DocsColumns blocks={blocks} goNext={goNext} goPrev={goPrev} hasNext={hasNext} hasPrev={hasPrev} nav={nav} nextTitle={nextTitle} pageGroup={pageGroup} pageLead={pageLead} pageTitle={pageTitle} prevTitle={prevTitle} toc={toc} />
</div>
  </>);
}
