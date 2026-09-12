import ArticleHeading from "./ArticleHeading";
import ArticleBlocks from "./ArticleBlocks";
import ArticlePagination from "./ArticlePagination";

export default function DocsArticle({ blocks, goNext, goPrev, hasNext, hasPrev, nextTitle, pageGroup, pageLead, pageTitle, prevTitle }) {
  return (<>
<div style={{"padding": "40px 36px 96px", "minWidth": "0"}}>
<ArticleHeading pageGroup={pageGroup} pageTitle={pageTitle} />
<h1 style={{"margin": "14px 0 12px", "fontSize": "40px", "lineHeight": "1.08", "letterSpacing": "-.04em", "fontWeight": "500"}} className="page-heading">
{pageTitle}
</h1>
<p style={{"margin": "0 0 32px", "fontSize": "16px", "lineHeight": "1.65", "color": "rgba(255,255,255,.55)", "maxWidth": "68ch", "textWrap": "pretty"}}>
{pageLead}
</p>
<ArticleBlocks blocks={blocks} />
<ArticlePagination goNext={goNext} goPrev={goPrev} hasNext={hasNext} hasPrev={hasPrev} nextTitle={nextTitle} prevTitle={prevTitle} />
</div>
  </>);
}
