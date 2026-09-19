const content = {
  group: "Advanced",
  title: "Data and configuration",
  lead: "The current version uses Settings screens, system preferences, and local storage. It does not read config.toml.",
  blocks: [
    { type: "h2", text: "Settings" },
    { type: "para", text: "Use Platforms, AI, Voice, Agent, and Data in the interface. This flow does not implement profiles through --profile, automatic TOML reloads, or the ROCKY_CONFIG, ROCKY_DATA_DIR, and ROCKY_LOG variables." },
    { type: "h2", text: "Storage" },
    { type: "para", text: "On macOS, data is stored under ~/Library/Application Support/Rocky; on Windows, under %APPDATA%/Rocky. Non-secret preferences, including the TikTok channel username, use the dev/rocky namespace. AI and ElevenLabs keys and Kick and YouTube Client Secrets use the system vault; platform tokens stay in memory. Rocky does not encrypt SQLite." },
    { type: "h2", text: "Backup and import" },
    { type: "para", text: "Under Data, export a JSON backup of records and sources without credentials. Import asks for confirmation, ignores identical copies, and stops on conflicting IDs; it does not replace existing records. Close Rocky before copying the database directly." },
    { type: "h2", text: "Diagnostics and updates" },
    { type: "para", text: "Data lets you view and export diagnostics without messages or keys. Update checks access GitHub and can be disabled in settings. Download update selects the compatible package, shows progress, and verifies the published SHA-256. You can cancel and retry. Disconnect platforms and click Update and restart to install and reopen Rocky, preserving data and saved credentials. Older versions without this button need the release with the built-in updater installed once through the official installer. Reset settings is a separate action that removes saved credentials and ends the session; reopen the app to apply every default." }
  ]
};

export default content;
