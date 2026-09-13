const content = {
  group: "Advanced",
  title: "Plugins",
  lead: "The current desktop application does not load user plugins.",
  blocks: [
    { type: "h2", text: "No plugin API" },
    { type: "para", text: "There is no implemented ~/.rocky/plugins directory, JavaScript event system, speak/send/note/suppress actions, or OBS integration through plugins." },
    { type: "h2", text: "Contribute integrations" },
    { type: "para", text: "New capabilities must be developed in the Kotlin Multiplatform repository and connected to existing contracts and UI. Discuss the proposal in an issue before implementing a broad change." }
  ]
};

export default content;
