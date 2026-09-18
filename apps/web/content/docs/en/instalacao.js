const content = {
  group: "Getting started",
  title: "Installation",
  lead: "Install Rocky for macOS or Windows from opensources-com-br/rocky releases. Versions with the built-in updater let you install future releases from the app itself.",
  blocks: [
    { type: "h2", text: "Installers" },
    { type: "para", text: "On macOS, choose the arm64 DMG for Apple Silicon or x86_64 for Intel, then copy Rocky to Applications before opening it. On Windows, use the amd64/x64 MSI or EXE. The download button uses the architecture reported by your browser; if it cannot identify it, the official release page opens for you to choose. Check the release SHA-256 checksums." },
    { type: "h2", text: "Run from source" },
    { type: "para", text: "With Git and JDK 17 installed, clone https://github.com/opensources-com-br/rocky.git. From the root, run ./gradlew :apps:desktop:run on macOS or .\\gradlew.bat :apps:desktop:run in PowerShell. The desktop app uses Kotlin and Compose Multiplatform; Node.js is used by the web app." },
    { type: "h2", text: "Permissions" },
    { type: "para", text: "Voice is optional and requires microphone access. Global shortcuts depend on system permissions and availability; check their registration status in Voice settings. You can start with quick analysis actions without configuring a microphone." },
    { type: "h2", text: "Open the current alpha on macOS" },
    { type: "para", text: "After trying to open Rocky, go to System Settings → Privacy & Security, find the Rocky notice, and click Open Anyway. Use this exception only if you downloaded the DMG from the official release and checked its published SHA-256. The next release built with distribution credentials will be signed and notarized." },
    { type: "h2", text: "Systems" },
    { type: "para", text: "The project packages macOS and Windows. The current flow does not offer a Linux installer. Check the requirements and architecture of the selected artifact before installing." }
  ]
};

export default content;
