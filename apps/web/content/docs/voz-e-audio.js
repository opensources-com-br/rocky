const content = {
  group: "Configuração",
  lead: "A voz do Rocky sai apenas no seu monitor — ela nunca entra no áudio da transmissão. Você escolhe o motor de síntese, o timbre, a velocidade e o volume.",
  blocks: [
    {"type": "h2", "text": "Motores de síntese"},
    {"type": "cards", "items": [{"key": "system", "text": "Vozes do macOS. Zero configuração, funciona offline."}, {"key": "piper", "text": "Síntese neural local. Melhor qualidade, roda sem internet."}, {"key": "elevenlabs", "text": "Provedor externo. Requer sua chave e conexão."}]},
    {"type": "h2", "text": "Configuração"},
    {"type": "code", "file": "~/.rocky/config.toml", "text": "[voice]\n# system | piper | elevenlabs\nengine = \"piper\"\nvoice  = \"pt_BR-faber\"\nrate   = 1.0\nmonitor_volume = 0.7\nducking = true"},
    {"type": "h2", "text": "Ducking"},
    {"type": "para", "text": "Com ducking ligado, o Rocky abaixa temporariamente o volume da sua música de fundo local enquanto fala, para você ouvir a intervenção sem esforço. Isso afeta apenas a sua saída de monitor, não a mixagem que vai ao ar."},
    {"type": "h2", "text": "Entrada de voz"},
    {"type": "para", "text": "O reconhecimento de fala roda local por padrão, usando o motor do sistema. Rocky só escuta enquanto o atalho está pressionado — não há escuta contínua e nenhum áudio é gravado em disco."},
    {"type": "note", "text": "Se você usa uma interface de áudio com vários canais, escolha explicitamente a saída de monitor em Configurações → Voz. Por padrão o app usa a saída do sistema, que pode ser a mesma que vai para a live."},
  ],
};

export default content;
