import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import React from "react";

export default function ContributionFields({ fields }) {
  return (<>
{fields.map((f, index) => <React.Fragment key={f?.id ?? f?.name ?? index}>
<div style={{"display": "flex", "flexDirection": "column", "gap": "8px"}}>
<span style={{"fontSize": "13px", "color": "rgba(255,255,255,.8)"}}>
{f.label}
</span>
<span style={{"fontSize": "12.5px", "lineHeight": "1.5", "color": "rgba(255,255,255,.38)", "marginTop": "-4px"}}>
{f.hint}
</span>
{Boolean(f.isText) && <>
<Input value={f.value} onChange={f.onInput} placeholder={f.placeholder} style={{"padding": "10px 12px", "borderRadius": "8px", "background": "rgba(255,255,255,.04)", "border": "1px solid rgba(255,255,255,.09)", "fontFamily": "var(--font-geist-sans), sans-serif", "fontSize": "13.5px", "color": "#f2efec", "outline": "none"}} aria-label={f.label} />
</>}
{Boolean(f.isArea) && <>
<Textarea value={f.value} onChange={f.onInput} placeholder={f.placeholder} rows={f.rows} style={{"padding": "11px 12px", "borderRadius": "8px", "background": "rgba(255,255,255,.04)", "border": "1px solid rgba(255,255,255,.09)", "fontFamily": "var(--font-geist-sans), sans-serif", "fontSize": "13.5px", "lineHeight": "1.6", "color": "#f2efec", "outline": "none", "resize": "vertical"}} aria-label={f.label}>

</Textarea>
</>}
{Boolean(f.isChoice) && <>
<div style={{"display": "flex", "flexWrap": "wrap", "gap": "6px"}}>
{f.options.map((o, index) => <React.Fragment key={o?.id ?? o?.name ?? index}>
<Button variant="ghost" className="reference-button" type="button" style={{"padding": "6px 11px", "borderRadius": "7px", "cursor": "pointer", "fontSize": "12.5px", "background": o.bg, "border": "1px solid " + o.border, "color": o.color}} onClick={o.pick}>
{o.name}
</Button>
</React.Fragment>)}
</div>
</>}
{Boolean(f.isUrls) && <>
<div style={{"display": "flex", "flexDirection": "column", "gap": "8px"}}>
{f.items.map((u, index) => <React.Fragment key={u?.id ?? u?.name ?? index}>
<div style={{"display": "flex", "alignItems": "center", "gap": "9px", "padding": "0 0 0 11px", "borderRadius": "8px", "background": "rgba(255,255,255,.04)", "border": "1px solid rgba(255,255,255,.09)"}}>
<span style={{"width": "7px", "height": "7px", "borderRadius": "50%", "background": u.dot, "flex": "none"}}>

</span>
<span style={{"fontSize": "12.5px", "color": "rgba(255,255,255,.45)", "minWidth": "62px"}}>
{u.name}
</span>
<Input value={u.value} onChange={u.onInput} placeholder={u.placeholder} style={{"flex": "1", "minWidth": "0", "padding": "10px 12px 10px 0", "background": "transparent", "border": "0", "fontFamily": "var(--font-geist-mono), monospace", "fontSize": "12.5px", "color": "#f2efec", "outline": "none"}} aria-label={u.name} />
</div>
</React.Fragment>)}
</div>
</>}
{Boolean(f.isPlatforms) && <>
<div style={{"display": "flex", "flexWrap": "wrap", "gap": "6px"}}>
{f.options.map((o, index) => <React.Fragment key={o?.id ?? o?.name ?? index}>
<Button variant="ghost" className="reference-button" type="button" style={{"display": "flex", "alignItems": "center", "gap": "7px", "padding": "6px 11px 6px 9px", "borderRadius": "7px", "cursor": "pointer", "fontSize": "12.5px", "background": o.bg, "border": "1px solid " + o.border, "color": o.color}} onClick={o.pick}>
<span style={{"width": "7px", "height": "7px", "borderRadius": "50%", "background": o.dot}}>

</span>
{o.name + " "}
</Button>
</React.Fragment>)}
</div>
</>}
</div>
</React.Fragment>)}
  </>);
}
