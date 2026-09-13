const messages = [
  ["ana", "Qual é o preço do curso?"],
  ["pedro", "Vai ficar gravado também?"],
  ["julia", "Faz uma live sobre esse assunto!"],
  ["lucas", "Também queria saber quanto custa."],
  ["marina", "Uma live só de perguntas seria legal!"],
];

export default function AppConversation() {
  return <div className="rocky-conversation">
    <div className="rocky-conversation-tools"><span>Histórico</span><span>Perguntas (2)</span><small>Amostra · detalhes</small><small>{messages.length} mensagens</small></div>
    <div className="rocky-quick-questions">{["Dúvidas principais", "O que perdi?", "Ideias do chat"].map(label => <span key={label}>{label}</span>)}</div>
    <div className="rocky-chat">
      <div className="rocky-chat-messages">{messages.map(([author, text]) =>
        <div className="rocky-chat-message" key={author}>
          <i aria-hidden="true" />
          <div><div className="rocky-chat-author"><strong>{author}</strong><small>Twitch</small></div><p>{text}</p></div>
        </div>
      )}</div>
      <div className="rocky-chat-scrollbar" aria-hidden="true"><span /></div>
    </div>
  </div>;
}
