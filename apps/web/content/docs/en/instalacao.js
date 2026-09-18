const content = {
  group: "Getting started",
  title: "Installation",
  lead: "Install Rocky for macOS or Windows from opensources-com-br/rocky releases. Versions with the built-in updater let you install future releases from the app itself.",
  blocks: [
    { type: "h2", text: "Installers" },
    { type: "para", text: "On macOS, use the DMG that matches your machine; on Windows, use MSI or EXE. Check the files and SHA-256 checksums for the selected release. The system may display security warnings because the installers are unsigned. This project does not document a Homebrew distribution for Rocky." },
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
