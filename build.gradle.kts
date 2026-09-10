plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.compose) apply false
}

allprojects {
    group = "dev.rocky"
    version = providers.gradleProperty("rockyVersion").get()
}
