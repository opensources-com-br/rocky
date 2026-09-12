export function Chord({ compact = false }) {
  const rows = [3, 1, 4, 2, 0];
  const step = compact ? 4.5 : 5;
  return <span aria-hidden="true" style={{ position: "relative", display: "inline-block", width: compact ? 70 : 84, height: compact ? 24 : 28 }}>
    {rows.map((_, i) => <span key={`line-${i}`} style={{ position: "absolute", left: 0, right: 0, top: (compact ? 2 : 3) + i * step, height: 1, background: "rgba(212,112,60,.3)" }} />)}
    {rows.map((r, i) => <span key={`note-${i}`} style={{ position: "absolute", left: (compact ? 4 : 5) + i * (compact ? 14 : 17), top: r * step, width: compact ? 6 : 7, height: compact ? 6 : 7, borderRadius: "50%", background: "#d4703c", animation: `livepulse ${1000 + i * 130}ms ease-in-out infinite`, animationDelay: `${i * 90}ms` }} />)}
  </span>;
}
export function MicrophoneWave() {
  return <span aria-hidden="true" style={{ display: "inline-flex", alignItems: "center", gap: 3, height: 20 }}>
    {[7, 14, 19, 11, 16].map((height, i) => <span key={i} style={{ display: "block", width: 3, height, borderRadius: 2, background: "#d4703c", animation: `livepulse ${800 + i * 120}ms ease-in-out infinite` }} />)}
  </span>;
}
