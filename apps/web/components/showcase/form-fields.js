import { TW, KI, YT, FB } from "@/content/showcase";
export function formFields(f, setField, setUrl, togglePlatform) {
    const pill = (on) => ({
      bg: on ? "rgba(212,112,60,.14)" : "rgba(255,255,255,.03)",
      border: on ? "rgba(212,112,60,.45)" : "rgba(255,255,255,.09)",
      color: on ? "#e08b57" : "rgba(255,255,255,.55)",
    });
    const choice = (label, hint, key, options) => ({
      label, hint, isChoice: true,
      options: options.map((o) => Object.assign({ name: o, pick: () => setField(key, o) }, pill(f[key] === o))),
    });
    const text = (label, hint, key, placeholder) => ({
      label, hint, isText: true, value: f[key], placeholder,
      onInput: (e) => setField(key, e.target.value),
    });
    const area = (label, hint, key, placeholder, rows) => ({
      label, hint, isArea: true, value: f[key], placeholder, rows,
      onInput: (e) => setField(key, e.target.value),
    });

    const PLATS = [
      { name: "Twitch", dot: TW }, { name: "Kick", dot: KI },
      { name: "YouTube", dot: YT }, { name: "Facebook", dot: FB },
    ];


return [
        text("Nome ou canal", "Como você quer aparecer na página.", "name", "ex. ju.lia"),
        choice("Categoria", "Onde o seu caso entra nos filtros.", "category", ["Programação", "Games", "Educação", "Podcast", "Esportes", "Arte"]),
        {
          label: "Plataformas conectadas", hint: "As que você mantém ligadas durante a live.", isPlatforms: true,
          options: PLATS.map((p) => Object.assign({ name: p.name, dot: f.platforms.indexOf(p.name) === -1 ? "rgba(255,255,255,.22)" : p.dot, pick: () => togglePlatform(p.name) }, pill(f.platforms.indexOf(p.name) !== -1))),
        },
        {
          label: "Links dos seus canais", hint: "A URL de cada plataforma que você marcou acima.", isUrls: true,
          items: f.platforms.map((name) => ({
            name,
            dot: { Twitch: TW, Kick: KI, YouTube: YT, Facebook: FB }[name],
            value: (f.urls || {})[name] || "",
            placeholder: { Twitch: "https://twitch.tv/seucanal", Kick: "https://kick.com/seucanal", YouTube: "https://youtube.com/@seucanal", Facebook: "https://facebook.com/suapagina" }[name],
            onInput: (e) => setUrl(name, e.target.value),
          })),
        },
        choice("Provedor de IA", "O que você configurou em Configurações → IA.", "provider", ["ollama", "openai-compat", "anthropic", "openai"]),
        text("Modelo", "Opcional. Nome exato do modelo.", "model", "ex. llama3.1:8b"),
        choice("Voz", "Motor de síntese que você usa.", "voice", ["piper", "system", "elevenlabs"]),
        text("Uma frase", "A citação que aparece no card. Curta.", "quote", "ex. Parei de perder pergunta boa no meio do chat."),
        area("Resumo da sua live", "Duas linhas sobre o que você transmite.", "desc", "ex. Lives de 4h sobre backend, quatro plataformas somadas.", 2),
        area("Depoimento", "O texto completo. Separe parágrafos com uma linha em branco.", "story", "Conte como era antes, o que você configurou e o que mudou.", 9),
        area("O que mudou", "Uma mudança por linha.", "changes", "Dúvida repetida sem resposta praticamente desapareceu.", 4),
      ];
}
