pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "rocky"

include(
    ":apps:desktop",
    ":shared:core",
    ":shared:data",
    ":shared:ui",
    ":platform:desktop",
)
