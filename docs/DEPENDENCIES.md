# Native dependencies added for launch preparation

The desktop platform uses JNA/JNA Platform 5.19.1 for native credential storage. JNA is dual-licensed; this project uses its Apache-2.0 option. Upstream license notices remain in the dependency jars. See [JNA source and license](https://github.com/java-native-access/jna).

Windows uses user-scoped DPAPI through `Crypt32Util`; macOS uses generic-password Keychain APIs through the Security framework. No secret is passed in shell command arguments. Native API behavior still requires verification from the installed candidate on both systems.

The managed Whisper model is `ggml-base.bin` from [ggerganov/whisper.cpp, revision 5359861c739e955e79d9a303bcbc70fb988958b1](https://huggingface.co/ggerganov/whisper.cpp/tree/5359861c739e955e79d9a303bcbc70fb988958b1). The recorded LFS SHA-256 is `60ed5bc3dd14eea856493d334349b405782ddcaf0028d4b5df4088345fba2efe`, size 147951465 bytes. It is downloaded during optional setup, not bundled in the installer. Homebrew installs the external whisper.cpp runtime on supported Macs; Windows setup remains manual.

The existing Kotlin, Compose, SQLDelight, SQLite driver and serialization dependencies remain Gradle-managed. Run `./gradlew :apps:desktop:dependencies` to inspect the resolved graph. This note is not a full dependency vulnerability audit or a substitute for the licenses of externally installed models and voices.
