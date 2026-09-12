import CaseSettings from "./CaseSettings";
import CasePreview from "./CasePreview";

export default function CaseConfiguration({ chord, d }) {
  return (<>
<div style={{"display": "flex", "flexDirection": "column", "gap": "20px", "minWidth": "0"}}>
<CaseSettings d={d} />
<div style={{"borderRadius": "12px", "border": "1px solid rgba(255,255,255,.09)", "overflow": "hidden"}}>
<div style={{"padding": "11px 15px", "borderBottom": "1px solid rgba(255,255,255,.07)", "background": "#0c0a09", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "11.5px", "color": "rgba(255,255,255,.4)"}}>
{"config.toml"}
</div>
<div style={{"padding": "15px 15px 18px", "background": "#0a0908", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "12px", "lineHeight": "1.7", "color": "rgba(255,255,255,.72)", "whiteSpace": "pre", "overflowX": "auto"}}>
{d.config}
</div>
</div>
<CasePreview chord={chord} d={d} />
</div>
  </>);
}
