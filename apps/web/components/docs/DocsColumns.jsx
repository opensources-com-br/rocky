import SiteFrame from "@/components/SiteFrame";
import DocsArticle from "./DocsArticle";
import DocsSidebar from "./DocsSidebar";
import DocsToc from "./DocsToc";

export default function DocsColumns({ blocks, goNext, goPrev, hasNext, hasPrev, locale, nav, nextTitle, pageGroup, pageLead, pageTitle, prevTitle, toc }) {
  return (<>
<SiteFrame className="docs-columns">
<DocsSidebar locale={locale} nav={nav} pageTitle={pageTitle} />
<DocsArticle blocks={blocks} goNext={goNext} goPrev={goPrev} hasNext={hasNext} hasPrev={hasPrev} locale={locale} nextTitle={nextTitle} pageGroup={pageGroup} pageLead={pageLead} pageTitle={pageTitle} prevTitle={prevTitle} />
<DocsToc locale={locale} toc={toc} />
</SiteFrame>
  </>);
}
