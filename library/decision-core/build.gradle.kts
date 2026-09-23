plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("maven-publish")
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            name = "localValidation"
            url = uri(layout.buildDirectory.dir("publishing-validation"))
        }
    }

    publications {
        create<MavenPublication>("release") {
            artifactId = "decision-core"
            from(components["java"])
            pom {
                name.set("Jev Android Toolkit - decision-core")
                description.set("Core typed Jev decision models and confidence policies.")
                url.set("https://github.com/Skbindas/jev-android-toolkit")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit")
                    }
                }
                scm {
                    url.set("https://github.com/Skbindas/jev-android-toolkit")
                    connection.set("scm:git:https://github.com/Skbindas/jev-android-toolkit.git")
                    developerConnection.set("scm:git:ssh://git@github.com/Skbindas/jev-android-toolkit.git")
                }
            }
        }
    }
}
