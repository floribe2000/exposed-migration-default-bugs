rootProject.name = "exposed-migration-default-bugs"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(":jsonb-default")
include(":transform-default")
