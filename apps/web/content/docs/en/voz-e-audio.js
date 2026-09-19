const content = {
  group: "Configuration",
  title: "Voice and audio",
  lead: "Voice is optional. Rocky uses system speech or ElevenLabs to answer and local whisper.cpp to transcribe the microphone.",
  blocks: [
    { type: "h2", text: "Voice output" },
    { type: "para", text: "Under Voice, choose the provider and test playback. System voice uses say on macOS and System.Speech on Windows. On Mac, this option uses system volume. ElevenLabs allows volume control in the app. Piper, ducking, and output selection are not implemented." },
    { type: "h2", text: "ElevenLabs" },
    { type: "para", text: "Select ElevenLabs, save your key, and click Load voices and models. Choose both or enter their IDs manually; the initial model is eleven_flash_v2_5. The key uses the system vault. Tests and answers send text to ElevenLabs and may consume credits. Audio plays as it arrives; key, permission, and quota failures appear in the panel. The local fallback is optional and only applies before playback starts." },
    { type: "h2", text: "Recognition" },
    { type: "para", text: "On macOS with Homebrew, Set up voice recognition installs whisper.cpp and downloads the managed model. On Windows, manually select the whisper-cli executable, GGML model, and microphone. Setup requires network access; transcription is local." },
    { type: "h2", text: "How to speak" },
    { type: "para", text: "With voice prepared, the listener starts when Twitch, Kick, YouTube, Facebook, or TikTok LIVE connects and can be toggled from the footer. Say “Rocky” followed by your request. The configured agent name acts as the wake word. Ctrl + Shift + F8 interrupts speech and captures a direct command without the wake word. For 12 seconds after an answer, follow-ups such as “explain that,” “summarize that,” and “save that as a note” do not require the name. A new question replaces the previous analysis." },
    { type: "h2", text: "End of speech" },
    { type: "para", text: "Capture ends after speech followed by 450 to 1500 ms of silence. Ongoing phrases can last up to 30 seconds; without detection, capture uses eight-second blocks. Calibrate noise under Voice or adjust the threshold. The microphone keeps a buffer during transcription and listens during analysis; it pauses during playback to avoid hearing its own voice. Use the speech shortcut to interrupt and rephrase." },
    { type: "h2", text: "Audio and OBS" },
    { type: "para", text: "The temporary WAV is deleted when transcription ends, including handled errors; a crash may leave system temporary files. Raw audio is not sent to the AI provider. Use headphones and make a test recording: desktop audio capture may include Rocky in the stream. A private monitor is not guaranteed." }
  ]
};

export default content;
