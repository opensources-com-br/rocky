import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm("desktop") {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:core"))
            implementation(libs.sqldelight.runtime)
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)
            }
        }
    }
}

sqldelight {
    databases {
        create("RockyDatabase") {
            packageName.set("dev.rocky.data.db")
        }
    }
}
