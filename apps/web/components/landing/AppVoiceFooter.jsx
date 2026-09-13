import { localized } from "@/lib/i18n";

export default function AppVoiceFooter({ locale }) {
  return <footer className="rocky-voice-footer">
    <div className="rocky-voice-wave" aria-hidden="true">{[4, 7, 10, 7, 4].map((height, index) => <i key={index} style={{ height }} />)}</div>
    <div className="rocky-voice-label"><span>{localized(locale, "Microphone listening", "Microfone ouvindo")}</span><small>{localized(locale, "Speak now · level 0%", "Fale agora · nível 0%")}</small></div>
    <div><strong>321</strong><small>{localized(locale, "watching", "assistindo")}</small></div>
    <div className="rocky-message-metric"><strong>18</strong><small>msg/min</small></div>
  </footer>;
}
