import { AudioLines } from "lucide-react";
import localFont from "next/font/local";
import { Button } from "@/components/ui/button";
import { localized } from "@/lib/i18n";

const instrument = localFont({ src: "../../app/fonts/instrument-serif.ttf" });

export default function AppAnswer({ locale }) {
  return <section className="rocky-answer">
    <div className="rocky-answer-label"><span>♪ {localized(locale, "ROCKY SUGGESTION", "SUGESTÃO DO ROCKY")}</span><AudioLines aria-hidden="true" /></div>
    <p className={instrument.className}>{localized(locale, "Two people asked about the course price. It may be worth answering now.", "Duas pessoas perguntaram o preço do curso. Vale responder agora.")}</p>
    <div className="rocky-sources"><span>●</span> {localized(locale, "2 related questions · Twitch + Facebook", "2 perguntas relacionadas · Twitch + Facebook")} <span className="rocky-evidence">{localized(locale, "View sources", "Ver fontes")}</span></div>
    <div className="rocky-answer-actions">
      <Button tabIndex={-1}>{localized(locale, "Save as note", "Salvar como nota")}</Button>
      <Button variant="outline" tabIndex={-1}>{localized(locale, "Next", "Próxima")}</Button>
      <Button variant="outline" tabIndex={-1}>{localized(locale, "Mute", "Silenciar")}</Button>
    </div>
  </section>;
}
