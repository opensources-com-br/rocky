export default function AppVoiceFooter() {
  return <footer className="rocky-voice-footer">
    <div className="rocky-voice-wave" aria-hidden="true">{[4, 7, 10, 7, 4].map((height, index) => <i key={index} style={{ height }} />)}</div>
    <div className="rocky-voice-label"><span>Ativar ouvinte do Rocky</span><small>clique para ativar</small></div>
    <div><strong>321</strong><small>assistindo</small></div>
    <div className="rocky-message-metric"><strong>18</strong><small>msg/min</small></div>
  </footer>;
}
