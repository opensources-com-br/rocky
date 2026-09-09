pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
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
