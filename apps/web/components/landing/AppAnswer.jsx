import localFont from "next/font/local";
import { Button } from "@/components/ui/button";

const instrument = localFont({ src: "../../../../../shared/ui/src/commonMain/composeResources/font/instrument_serif_regular.ttf" });

export default function AppAnswer({ chord }) {
  return <section className="rocky-answer">
    <div className="rocky-answer-label"><span>♪ SUGESTÃO DO ROCKY</span>{chord}</div>
    <p className={instrument.className}>Sete pessoas perguntaram o preço do curso. Vale responder agora.</p>
    <div className="rocky-sources"><span>●</span> 7 na Twitch <span className="rocky-evidence">Ver fontes</span></div>
    <div className="rocky-answer-actions">
      <Button tabIndex={-1}>Salvar como nota</Button>
      <Button variant="outline" tabIndex={-1}>Próxima</Button>
      <Button variant="outline" tabIndex={-1}>Silenciar</Button>
    </div>
  </section>;
}
