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
            from(components["java"])
        }
    }
}
