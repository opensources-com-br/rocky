import { CheckCircle2, Minimize2, Pin, Settings } from "lucide-react";
import { Badge } from "@/components/ui/badge";

export default function AppTitlebar() {
  return <>
    <div className="rocky-mac-titlebar" aria-hidden="true">
      <span /><span /><span /><strong>Rocky</strong>
    </div>
    <header className="rocky-app-header">
      <div><strong>Rocky</strong><small>a voz do chat, em acordes</small></div>
      <Badge variant="outline">CHAT ATIVO</Badge>
      <div className="rocky-header-icons" aria-hidden="true">
        <CheckCircle2 /><Minimize2 /><Pin /><Settings />
      </div>
    </header>
  </>;
}
