import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm("desktop") {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }
}
