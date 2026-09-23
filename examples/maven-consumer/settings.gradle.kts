pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("../../library/decision-modules/build/publishing-validation")
        }
        maven {
            url = uri("../../library/jev-client/build/publishing-validation")
        }
        maven {
            url = uri("../../library/decision-core/build/publishing-validation")
        }
        mavenCentral()
    }
}

rootProject.name = "jev-published-consumer-smoke"
