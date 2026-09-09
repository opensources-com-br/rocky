import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "dev.rocky.apps"

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    jvm {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:core"))
            implementation(project(":shared:data"))
            implementation(project(":shared:ui"))
            implementation(project(":platform:desktop"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.rocky.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe)
            packageName = "Rocky"
            packageVersion = "1.0.7"

            macOS {
                bundleID = "dev.rocky.app"
                infoPlist {
                    extraKeysRawXml = """
                        <key>NSMicrophoneUsageDescription</key>
                        <string>Rocky uses the microphone only while you record a command for local transcription.</string>
                    """.trimIndent()
                }
            }

            windows {
                menuGroup = "Rocky"
                shortcut = true
                upgradeUuid = "CD761319-DDDF-439F-BEAF-9616ED84E4AF"
            }
        }
    }
}
