plugins {
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.compose) apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
}

allprojects {
    group = "dev.rocky"
    version = providers.gradleProperty("rockyVersion").get()
}
