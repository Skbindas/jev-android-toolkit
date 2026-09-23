plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

dependencies {
    implementation(project(":library:decision-core"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("dev.skbindas.jev.benchmark.MainKt")
}
