import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    jvm("desktop") {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:core"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
        }
    }
}
