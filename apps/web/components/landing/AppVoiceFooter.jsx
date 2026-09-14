import { localized } from "@/lib/i18n";

export default function AppVoiceFooter({ locale, totalViewers }) {
  return <footer className="rocky-voice-footer">
    <div className="rocky-voice-wave" aria-hidden="true">{[4, 7, 10, 7, 4].map((height, index) => <i key={index} style={{ height }} />)}</div>
    <div className="rocky-voice-label"><span>{localized(locale, "Microphone listening", "Microfone ouvindo")}</span><small>{localized(locale, "Speak now · level 0%", "Fale agora · nível 0%")}</small></div>
    <div><strong>{totalViewers}</strong><small>{localized(locale, "total watching", "assistindo no total")}</small></div>
    <div className="rocky-message-metric"><strong>18</strong><small>msg/min</small></div>
  </footer>;
}
