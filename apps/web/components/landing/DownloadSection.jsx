import DownloadContent from "./DownloadContent";

export default function DownloadSection() {
  return (<>
<div style={{"position": "relative", "borderBottom": "1px solid rgba(255,255,255,.07)", "overflow": "hidden"}}>
<div style={{"position": "absolute", "inset": "0", "background": "radial-gradient(50% 100% at 50% 100%, rgba(212,112,60,.14), transparent 70%)", "pointerEvents": "none"}}>

</div>
<DownloadContent  />
</div>
  </>);
}
