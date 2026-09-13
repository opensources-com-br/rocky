import { CheckCircle2, Minimize2, Pin, Settings } from "lucide-react";
import { Badge } from "@/components/ui/badge";

export default function AppTitlebar({ platform = "macos" }) {
  return <>
    {platform === "macos" ? (
      <div className="rocky-mac-titlebar" aria-hidden="true">
        <span /><span /><span /><strong>Rocky</strong>
      </div>
    ) : (
      <div className="rocky-linux-titlebar" aria-hidden="true">
        <span className="rocky-linux-app-icon">R</span>
        <strong>Rocky</strong>
        <div className="rocky-linux-controls"><span>−</span><span>□</span><span>×</span></div>
      </div>
    )}
    <header className="rocky-app-header">
      <div><strong>Rocky</strong><small>a voz do chat, em acordes</small></div>
      <Badge variant="outline">MICROFONE ON</Badge>
      <div className="rocky-header-icons" aria-hidden="true">
        <CheckCircle2 /><Minimize2 /><Pin /><Settings />
      </div>
    </header>
  </>;
}
