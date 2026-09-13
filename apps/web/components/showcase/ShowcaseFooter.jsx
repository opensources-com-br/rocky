import SiteFrame from "@/components/SiteFrame";

export default function ShowcaseFooter() {
  return (<>
<SiteFrame className="site-footer" style={{"display": "flex", "flexWrap": "wrap", "gap": "16px", "alignItems": "center", "fontSize": "12.5px", "color": "rgba(255,255,255,.3)"}}>
<span style={{"fontFamily": "var(--font-geist-mono), monospace"}}>
{"Rocky © 2026"}
</span>
<div style={{"marginLeft": "auto", "display": "flex", "gap": "20px"}}>
<a href={"https://github.com/opensources-com-br/rocky"} style={{"color": "inherit"}}>
{"GitHub"}
</a>
<a href={"https://github.com/opensources-com-br/rocky"} style={{"color": "inherit"}}>
{"Licença MIT"}
</a>
<a href={"https://github.com/opensources-com-br/rocky/releases"} style={{"color": "inherit"}}>
{"O app"}
</a>
</div>
</SiteFrame>
  </>);
}
