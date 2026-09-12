import AppTitlebar from "./AppTitlebar";
import AppAnswer from "./AppAnswer";
import AppConversation from "./AppConversation";
import AppVoiceFooter from "./AppVoiceFooter";
import "./app-preview.css";

export default function AppPreview({ chord }) {
  return <figure className="rocky-preview">
    <div className="rocky-app" inert aria-label="Demonstração da interface desktop do Rocky">
      <AppTitlebar />
      <div className="rocky-session"><div><strong>CONEXÃO REAL · TWITCH</strong><small>@canal_exemplo</small></div><span>Desconectar</span></div>
      <div className="rocky-platforms">{["Twitch", "Kick", "YouTube", "Facebook"].map((name, index) => <span key={name} className={index === 0 ? "connected" : ""}><i />{name}</span>)}</div>
      <AppAnswer chord={chord} />
      <div className="rocky-tabs">{["Conversa", "Superchats", "Notas", "Ideias", "Pulso"].map((name, index) => <span key={name} className={index === 0 ? "selected" : ""}>{name}</span>)}</div>
      <AppConversation />
      <AppVoiceFooter />
    </div>
    <figcaption>Interface desktop do Rocky · demonstração com dados de exemplo</figcaption>
  </figure>;
}
