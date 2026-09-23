plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    api(project(":library:decision-core"))
    api(project(":library:jev-client"))
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            artifactId = "decision-modules"
            from(components["java"])
            pom {
                name.set("Jev Android Toolkit - decision-modules")
                description.set("Android decision modules for moderation, notifications, paywalls, reranking, and verification.")
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
