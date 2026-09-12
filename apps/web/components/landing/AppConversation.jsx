import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export default function AppConversation() {
  return <div className="rocky-conversation">
    <div className="rocky-conversation-tools"><span>Histórico</span><span>Perguntas (7)</span><small>Amostra · detalhes</small><small>24 mensagens</small></div>
    <div className="rocky-request"><Input aria-label="Pergunta de exemplo" placeholder="Pergunte ao Rocky sobre o chat…" readOnly tabIndex={-1} /><Button variant="outline" tabIndex={-1}>Perguntar</Button></div>
    <div className="rocky-quick-questions">{["Dúvidas principais", "O que perdi?", "Ideias do chat"].map(label => <span key={label}>{label}</span>)}</div>
    <div className="rocky-chat-message"><i /><strong>ana</strong><span>Qual é o preço do curso?</span><small>agora</small></div>
    <div className="rocky-chat-message"><i /><strong>pedro</strong><span>Vai ficar gravado também?</span><small>agora</small></div>
    <div className="rocky-chat-message"><i /><strong>julia</strong><span>Faz uma live sobre esse assunto!</span><small>agora</small></div>
  </div>;
}
