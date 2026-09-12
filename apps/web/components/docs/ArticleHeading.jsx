
export default function ArticleHeading({ pageGroup, pageTitle }) {
  return (<>
<div style={{"display": "flex", "alignItems": "center", "gap": "8px", "fontSize": "12.5px", "color": "rgba(255,255,255,.35)"}}>
<span>
{"Docs"}
</span>
<span style={{"opacity": ".5"}}>
{"/"}
</span>
<span>
{pageGroup}
</span>
<span style={{"opacity": ".5"}}>
{"/"}
</span>
<span style={{"color": "rgba(255,255,255,.6)"}}>
{pageTitle}
</span>
</div>
  </>);
}
