"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import AppTitlebar from "./AppTitlebar";
import AppAnswer from "./AppAnswer";
import AppConversation from "./AppConversation";
import AppVoiceFooter from "./AppVoiceFooter";
import "./app-preview.css";

export default function AppPreview() {
  const [platform, setPlatform] = useState("macos");
  const platformName = platform === "macos" ? "macOS" : "Windows";

  return <figure className="rocky-preview">
    <div className="rocky-preview-switch" role="group" aria-label="Sistema da prévia">
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
    <div className={`rocky-app rocky-app-${platform}`} inert aria-label={`Demonstração da interface do Rocky no ${platformName}`}>
      <AppTitlebar platform={platform} />
      <div className="rocky-platforms">{["Twitch", "Kick", "YouTube", "Facebook"].map(name => <span key={name} className={`available ${name.toLowerCase()}`}><i />{name}</span>)}</div>
      <AppAnswer />
      <div className="rocky-tabs">{["Conversa", "Superchats", "Notas", "Ideias", "Pulso"].map((name, index) => <span key={name} className={index === 0 ? "selected" : ""}>{name}</span>)}</div>
      <AppConversation />
      <AppVoiceFooter />
    </div>
    <figcaption>Interface do Rocky com moldura do {platformName} · demonstração com dados de exemplo</figcaption>
  </figure>;
}
