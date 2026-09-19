const content = {
  group: "Features",
  title: "Notes and ideas",
  lead: "Notes and ideas are local records that you create, edit, search, and organize by stream. Ideas are not generated and saved automatically.",
  blocks: [
    { type: "h2", text: "Save" },
    { type: "list", items: [
      "Use Save as note on the current suggestion, or save a History answer as a note or idea.",
      "Say “Rocky, save this as a note” or “Rocky, save this as an idea” to save the current answer.",
      "For free-form dictation: “Rocky, note: prepare a stream about Kotlin” or “Rocky, idea: interview a guest.”",
      "Saving keeps the current tab open and offers Undo. You can also create and edit records in the interface."
    ] },
    { type: "h2", text: "Organize" },
    { type: "para", text: "Search text, filter records by stream, and mark completed ideas. Questions detected by the queue are saved automatically; this does not mean Rocky records every promise or problem from the broadcast." },
    { type: "h2", text: "Moments" },
    { type: "para", text: "Say “Rocky, mark this moment: project demo.” The record stores the offset since the connected session began. It does not detect clips automatically or cut video, and its time can differ from the VOD if Rocky connects after the stream starts." },
    { type: "h2", text: "Persistence" },
    { type: "para", text: "Records stay in local SQLite, not in an automatic tree of Markdown files by session. Notes and ideas can be exported as Markdown, and Data offers JSON backup. Answer history is temporary; save anything you want to keep." }
  ]
};

export default content;
