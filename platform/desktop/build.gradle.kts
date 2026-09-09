import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "dev.rocky.platform"

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm("desktop") {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:core"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
