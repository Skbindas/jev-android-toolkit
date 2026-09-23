plugins {
    kotlin("jvm") version "2.2.10"
}

group = "dev.skbindas.jev.consumer"
version = "0.0.0"

dependencies {
    implementation("dev.skbindas.jev:decision-modules:0.1.0")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}
