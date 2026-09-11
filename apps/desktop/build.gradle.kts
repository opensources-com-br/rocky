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
        jvmArgs += "-Drocky.twitch.clientId=${providers.gradleProperty("rockyTwitchClientId").orElse("").get()}"

        nativeDistributions {
            modules("java.sql", "java.net.http")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe)
            packageName = "Rocky"
            packageVersion = providers.gradleProperty("rockyPackageVersion").get()

            macOS {
                bundleID = "dev.rocky.app"
                infoPlist {
                    extraKeysRawXml = """
                        <key>NSMicrophoneUsageDescription</key>
                        <string>Rocky listens locally for its wake word and transcribes your voice commands.</string>
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
