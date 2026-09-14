import { localized } from "@/lib/i18n";

const portugueseMessages = [
  ["ana", "Twitch", "Qual é o preço do curso?"],
  ["pedro", "Kick", "Vai ficar gravado também?"],
  ["julia", "YouTube", "Faz uma live sobre esse assunto!"],
  ["lucas", "Facebook", "Também queria saber quanto custa."],
  ["marina", "TikTok", "Uma live só de perguntas seria legal!"],
];

const englishMessages = [
  ["ana", "Twitch", "What is the course price?"],
  ["pedro", "Kick", "Will it be available as a recording too?"],
  ["julia", "YouTube", "Make a stream about this topic!"],
  ["lucas", "Facebook", "I would also like to know how much it costs."],
  ["marina", "TikTok", "A stream dedicated to questions would be great!"],
];

export default function AppConversation({ locale }) {
  const messages = localized(locale, englishMessages, portugueseMessages);
  return <div className="rocky-conversation">
    <div className="rocky-conversation-tools"><span>{localized(locale, "History", "Histórico")}</span><span>{localized(locale, "Questions (2)", "Perguntas (2)")}</span><small>{localized(locale, "Sample · details", "Amostra · detalhes")}</small><small>{messages.length} {localized(locale, "messages", "mensagens")}</small></div>
    <div className="rocky-quick-questions">{localized(locale, ["Top questions", "What did I miss?", "Chat ideas"], ["Dúvidas principais", "O que perdi?", "Ideias do chat"]).map(label => <span key={label}>{label}</span>)}</div>
    <div className="rocky-chat">
      <div className="rocky-chat-messages">{messages.map(([author, platform, text]) =>
        <div className="rocky-chat-message" key={author}>
          <i aria-hidden="true" />
          <div><div className="rocky-chat-author"><strong>{author}</strong><small>{platform}</small></div><p>{text}</p></div>
        </div>
      )}</div>
      <div className="rocky-chat-scrollbar" aria-hidden="true"><span /></div>
    </div>
  </div>;
}
