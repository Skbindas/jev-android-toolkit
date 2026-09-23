plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("maven-publish")
}

dependencies {
    api(project(":library:decision-core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
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
            artifactId = "jev-client"
            from(components["java"])
            pom {
                name.set("Jev Android Toolkit - jev-client")
                description.set("Kotlin client transport for TypeSafe Jev decision requests.")
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
