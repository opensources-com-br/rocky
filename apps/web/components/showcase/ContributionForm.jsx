import { Button } from "@/components/ui/button";
import ContributionFields from "./ContributionFields";

export default function ContributionForm({ copyFile, copyLabel, fields, readyLabel }) {
  return (<>
<div style={{"display": "flex", "flexDirection": "column", "gap": "22px", "minWidth": "0"}}>
<ContributionFields fields={fields} />
<div style={{"display": "flex", "flexWrap": "wrap", "alignItems": "center", "gap": "10px", "paddingTop": "6px"}}>
<Button variant="ghost" className="reference-button" type="button" style={{"padding": "11px 20px", "borderRadius": "9px", "background": "#f2efec", "color": "#0d0b0a", "fontSize": "13.5px", "fontWeight": "500", "cursor": "pointer"}} onClick={copyFile}>
{copyLabel}
</Button>
<a href={"https://github.com/opensources-com-br/rocky"} style={{"padding": "11px 20px", "borderRadius": "9px", "border": "1px solid rgba(255,255,255,.12)", "background": "rgba(255,255,255,.02)", "color": "rgba(255,255,255,.82)", "fontSize": "13.5px"}}>
{"Abrir o repositório"}
</a>
<span style={{"fontSize": "12.5px", "color": "rgba(255,255,255,.32)"}}>
{readyLabel}
</span>
</div>
</div>
  </>);
}
