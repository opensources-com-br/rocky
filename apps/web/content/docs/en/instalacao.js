const content = {
  group: "Getting started",
  title: "Installation",
  lead: "Download Rocky for macOS or Windows with the site button. It selects the compatible official installer; future releases can then be installed from the app itself.",
  blocks: [
    { type: "h2", text: "Installers" },
    { type: "para", text: "On macOS, choose the arm64 DMG for Apple Silicon or x86_64 for Intel, then copy Rocky to Applications before opening it. On Windows, use the amd64/x64 MSI or EXE. The download button uses the architecture reported by your browser; if it cannot identify it, the official release page opens for you to choose. Check the release SHA-256 checksums." },
    { type: "h2", text: "Run from source" },
    { type: "para", text: "With Git and JDK 17 installed, clone https://github.com/opensources-com-br/rocky.git. From the root, run ./gradlew :apps:desktop:run on macOS or .\\gradlew.bat :apps:desktop:run in PowerShell. The desktop app uses Kotlin and Compose Multiplatform; Node.js is used by the web app." },
    { type: "h2", text: "Permissions" },
    { type: "para", text: "Voice is optional and requires microphone access. Global shortcuts depend on system permissions and availability. You can start with quick analysis actions without configuring a microphone." },
    { type: "h2", text: "Update and restart" },
    { type: "para", text: "When a release is available, click Update or open Settings → Data → Download update. Rocky downloads the package for your architecture, shows progress, and verifies SHA-256. Then disconnect platforms and click Update and restart. Rocky installs the update and reopens, preserving your data, preferences, and saved credentials. On macOS, install Rocky at /Applications/Rocky.app or ~/Applications/Rocky.app with write access to the folder; without that permission, use the official installer. On Windows, the system may ask for permission to install the update." },
    { type: "para", text: "If your older version only offers Open installer, install the release with the built-in updater once over the existing installation, without uninstalling or deleting data. From that version onward, use Update and restart. Runs from source use the releases page to install the packaged app. If an update fails, check the message in Data and retry." },
    { type: "h2", text: "Site updates" },
    { type: "para", text: "This site contains the landing page, documentation, and showcase; Rocky runs in the desktop app. When a new site version is published, a notice offers Reload site or Later. The page only reloads when you click, and your language preference is preserved." },
    { type: "h2", text: "Systems" },
    { type: "para", text: "The project packages macOS and Windows. The current flow does not offer a Linux installer. Check the requirements and architecture of the selected artifact before installing." }
  ]
};

export default content;
