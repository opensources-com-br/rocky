const content = {
  group: "Configuration",
  title: "Agent behavior",
  lead: "Name, language, and tone are under Agent. Analysis profiles and filters are under AI.",
  blocks: [
    { type: "h2", text: "Profiles" },
    { type: "list", items: [
      "On demand: automatic analysis is disabled; use direct questions or Analyze now.",
      "Discreet: a minimum five-minute interval for automatic analysis.",
      "Proactive: a minimum two-minute interval for automatic analysis.",
      "Automatic analysis requires new messages. Direct questions take priority over automatic work."
    ] },
    { type: "h2", text: "Tone and name" },
    { type: "para", text: "Available tones are Direct, Upbeat, Analytical, and Ironic. They guide AI writing. The agent name is also used as the voice wake word; the interface language guides responses and transcription." },
    { type: "h2", text: "Limits" },
    { type: "para", text: "There are no configurable weights for money, relevance, or urgency. There is no fixed three-second pause. During voice requests, Rocky listens during analysis, waits for detected speech before answering, and accepts rephrasing. Capture pauses during playback; interrupt it with the speech shortcut. There is no acoustic echo cancellation." },
    { type: "h2", text: "History" },
    { type: "para", text: "History keeps up to 30 session answers, with copy, repeat, save note, and save idea actions. The last four pairs help understand requests such as “summarize that.” Disconnecting clears history; saved records remain." }
  ]
};

export default content;
