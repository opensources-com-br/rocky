import React from "react";
import CaseChanges from "./CaseChanges";

export default function CaseStory({ d }) {
  return (<>
<div style={{"display": "flex", "flexDirection": "column", "gap": "26px", "minWidth": "0"}}>
<blockquote style={{"margin": "0", "padding": "0 0 0 20px", "borderLeft": "2px solid #d4703c", "fontSize": "22px", "lineHeight": "1.45", "letterSpacing": "-.025em", "color": "#f2efec", "textWrap": "pretty"}}>
{"“" + d.quote + "”"}
</blockquote>
{d.story.map((par, index) => <React.Fragment key={par?.id ?? par?.name ?? index}>
<p style={{"margin": "0", "fontSize": "15.5px", "lineHeight": "1.72", "color": "rgba(255,255,255,.58)", "maxWidth": "62ch", "textWrap": "pretty"}}>
{par}
</p>
</React.Fragment>)}
<CaseChanges d={d} />
</div>
  </>);
}
