plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("maven-publish")
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    testImplementation(kotlin("test"))
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
