const content = {
  group: "Começando",
  lead: "Um roteiro de dez minutos para sair do zero até ter o Rocky falando com você durante a transmissão.",
  blocks: [
    {"type": "h2", "text": "Roteiro"},
    {"type": "steps", "items": [{"n": "01", "title": "Conecte um canal", "text": "Configurações → Plataformas. Comece com uma só; você adiciona as outras depois."}, {"n": "02", "title": "Escolha a IA", "text": "Configurações → IA. Cole a chave de um provedor, ou aponte para um modelo local já rodando."}, {"n": "03", "title": "Teste a voz", "text": "Configurações → Voz. O botão de teste fala uma frase no seu monitor para você ajustar volume e velocidade."}, {"n": "04", "title": "Posicione a janela", "text": "Arraste o Rocky para um canto que você veja sem virar a cabeça. A posição fica salva."}, {"n": "05", "title": "Abra a live e pergunte algo", "text": "Segure ⌥ Espaço e pergunte: “como está o chat?”. Se ele responder, está pronto."}]},
    {"type": "h2", "text": "Ajuste o volume de intervenções"},
    {"type": "para", "text": "Por padrão Rocky fala até três vezes a cada dez minutos. Na primeira live isso costuma parecer pouco ou demais, dependendo do tamanho do seu chat. Ajuste em Configurações → Agente até ele falar só quando você quer ser interrompido."},
    {"type": "h2", "text": "Atalhos que valem decorar"},
    {"type": "keys", "items": [{"keys": "⌥ Espaço", "text": "Segure para falar com o Rocky"}, {"keys": "⌥ M", "text": "Silenciar ou reativar a voz"}, {"keys": "⌥ N", "text": "Salvar a fala atual como nota"}, {"keys": "⌥ ⇧ R", "text": "Ler o próximo superchat da fila"}]},
    {"type": "note", "text": "Se o Rocky não responder, confira se o modelo escolhido está acessível: Configurações → IA mostra o estado da conexão em tempo real."},
  ],
};

export default content;
