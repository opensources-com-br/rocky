plugins {
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.compose) apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
}

dependencies {
    kover(project(":apps:desktop"))
    kover(project(":shared:core"))
    kover(project(":shared:data"))
    kover(project(":shared:ui"))
    kover(project(":platform:desktop"))
}

kover {
    reports {
        verify {
            rule { minBound(80) }
        }
    }
}

allprojects {
    group = "dev.rocky"
    version = providers.gradleProperty("rockyVersion").get()
}
