"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { localized } from "@/lib/i18n";
import AppTitlebar from "./AppTitlebar";
import AppAnswer from "./AppAnswer";
import AppConversation from "./AppConversation";
import AppVoiceFooter from "./AppVoiceFooter";
import "./app-preview.css";

const onlinePlatforms = [
  { name: "Twitch", viewers: 128 },
  { name: "Kick", viewers: 47 },
  { name: "YouTube", viewers: 82 },
  { name: "Facebook", viewers: 34 },
  { name: "TikTok", viewers: 30 },
];

export default function AppPreview({ locale }) {
  const [platform, setPlatform] = useState("macos");
  const platformName = platform === "macos" ? "macOS" : "Windows";
  const totalViewers = onlinePlatforms.reduce((total, item) => total + item.viewers, 0);

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
      <div className="rocky-platforms">
        <strong className="rocky-platform-status"><i />{localized(locale, "5 PLATFORMS ONLINE", "5 PLATAFORMAS ONLINE")}</strong>
        {onlinePlatforms.map(({ name, viewers }) => <span key={name} className={`available ${name.toLowerCase()}`}><i />{name}<small>{viewers}</small></span>)}
      </div>
      <AppAnswer locale={locale} />
      <div className="rocky-tabs">{localized(locale, ["Conversation", "Superchats", "Notes", "Ideas", "Pulse"], ["Conversa", "Superchats", "Notas", "Ideias", "Pulso"]).map((name, index) => <span key={name} className={index === 0 ? "selected" : ""}>{name}</span>)}</div>
      <AppConversation locale={locale} />
      <AppVoiceFooter locale={locale} totalViewers={totalViewers} />
    </div>
    <figcaption>{localized(locale, `Rocky interface with a ${platformName} frame · demo with sample data`, `Interface do Rocky com moldura do ${platformName} · demonstração com dados de exemplo`)}</figcaption>
  </figure>;
}
