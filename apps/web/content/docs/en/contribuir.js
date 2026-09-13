const content = {
  group: "Advanced",
  title: "Contributing",
  lead: "The project uses the MIT license, Kotlin Multiplatform for desktop, and Next.js for the web app.",
  blocks: [
    { type: "h2", text: "Run" },
    { type: "para", text: "Clone https://github.com/opensources-com-br/rocky.git. With JDK 17, run ./gradlew :apps:desktop:run from the root (gradlew.bat on Windows). For the web app, enter apps/web, run npm ci, and then npm run dev." },
    { type: "h2", text: "Validate" },
    { type: "list", items: [
      "Desktop: ./gradlew build, or .\\gradlew.bat build on Windows.",
      "Web: npm run lint, npm test, and npm run build inside apps/web.",
      "Make small commits and describe changes to the experience. For visual changes, include screenshots and verify window sizes."
    ] },
    { type: "h2", text: "Usage stories" },
    { type: "para", text: "The Submit yours section in Showcase is temporarily disabled. The form and draft generation are not available yet. Real stories must have the author's authorization and verifiable information; never include keys, tokens, or private data." },
    { type: "h2", text: "Scope" },
    { type: "para", text: "New platforms, support events, and other providers require implementation and tests; visual examples do not make those integrations available." }
  ]
};

export default content;
