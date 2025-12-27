rootProject.name = "clicky"

pluginManagement {
    includeBuild("tailwind-gradle")
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":server")
