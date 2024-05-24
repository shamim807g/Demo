pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Lengo"
include(":app")
include(":core:model")
include(":core:database")
include(":core:common")
include(":core:data")
include(":core:network")
include(":core:preferences")
include(":core:modalsheet")
