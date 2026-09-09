plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.compose) apply false
}

allprojects {
    group = "dev.rocky"
    version = "0.1.0-SNAPSHOT"
}
