"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { localized } from "@/lib/i18n";
import AppTitlebar from "./AppTitlebar";
import AppAnswer from "./AppAnswer";
import AppConversation from "./AppConversation";
import AppVoiceFooter from "./AppVoiceFooter";
import "./app-preview.css";

export default function AppPreview({ locale }) {
  const [platform, setPlatform] = useState("macos");
  const platformName = platform === "macos" ? "macOS" : "Windows";

  return <figure className="rocky-preview">
    <div className="rocky-preview-switch" role="group" aria-label={localized(locale, "Preview system", "Sistema da prévia")}>
      {[{ id: "macos", label: "macOS" }, { id: "windows", label: "Windows" }].map(option => (
        <Button
          key={option.id}
          type="button"
          variant={platform === option.id ? "secondary" : "ghost"}
          size="sm"
          aria-pressed={platform === option.id}
          onClick={() => setPlatform(option.id)}
        >
          {option.label}
        </Button>
      ))}
    </div>
    <div className={`rocky-app rocky-app-${platform}`} inert aria-label={localized(locale, `Rocky interface demo on ${platformName}`, `Demonstração da interface do Rocky no ${platformName}`)}>
      <AppTitlebar locale={locale} platform={platform} />
      <div className="rocky-platforms">{["Twitch", "Kick", "YouTube", "Facebook"].map(name => <span key={name} className={`available ${name.toLowerCase()}`}><i />{name}</span>)}</div>
      <AppAnswer locale={locale} />
      <div className="rocky-tabs">{localized(locale, ["Conversation", "Superchats", "Notes", "Ideas", "Pulse"], ["Conversa", "Superchats", "Notas", "Ideias", "Pulso"]).map((name, index) => <span key={name} className={index === 0 ? "selected" : ""}>{name}</span>)}</div>
      <AppConversation locale={locale} />
      <AppVoiceFooter locale={locale} />
    </div>
    <figcaption>{localized(locale, `Rocky interface with a ${platformName} frame · demo with sample data`, `Interface do Rocky com moldura do ${platformName} · demonstração com dados de exemplo`)}</figcaption>
  </figure>;
}
