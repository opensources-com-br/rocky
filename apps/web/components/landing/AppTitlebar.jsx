import { CheckCircle2, Minimize2, Pin, Settings } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { localized } from "@/lib/i18n";

export default function AppTitlebar({ locale, platform = "macos" }) {
  return <>
    {platform === "macos" ? (
      <div className="rocky-mac-titlebar" aria-hidden="true">
        <span /><span /><span /><strong>Rocky</strong>
      </div>
    ) : (
      <div className="rocky-windows-titlebar" aria-hidden="true">
        <span className="rocky-windows-app-icon">R</span>
        <strong>Rocky</strong>
        <div className="rocky-windows-controls"><span>−</span><span>□</span><span>×</span></div>
      </div>
    )}
    <header className="rocky-app-header">
      <div><strong>Rocky</strong><small>{localized(locale, "the voice of chat, in chords", "a voz do chat, em acordes")}</small></div>
      <Badge variant="outline">{localized(locale, "MICROPHONE ON", "MICROFONE ON")}</Badge>
      <div className="rocky-header-icons" aria-hidden="true">
        <CheckCircle2 /><Minimize2 /><Pin /><Settings />
      </div>
    </header>
  </>;
}
