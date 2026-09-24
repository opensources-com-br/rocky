const content = {
  group: "Features",
  title: "Post-stream summary",
  lead: "When every platform disconnects or you close the app, Rocky creates a local summary of the session records, which may span several streams.",
  blocks: [
    { type: "h2", text: "Content" },
    { type: "para", text: "The summary gathers saved session records, including notes, ideas, moments, and pending questions. It can only summarize what was recorded; it does not analyze the recording or recover the full chat." },
    { type: "h2", text: "How it is produced" },
    { type: "para", text: "Code builds the summary from records without calling the AI model. It is saved as a local SQLite record. There are no editable summary templates or automatically generated summary.md file." },
    { type: "h2", text: "Limits" },
    { type: "para", text: "It does not include automatic analysis of spikes, technical drops, or best clips. Moments are the ones you marked and are relative to Rocky's connection. Keep question and idea states updated so the summary reflects your pending items." }
  ]
};

export default content;
