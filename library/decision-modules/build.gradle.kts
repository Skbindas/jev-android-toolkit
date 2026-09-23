import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.authentication.http.HttpHeaderAuthentication

plugins {
    kotlin("jvm")
    id("maven-publish")
    id("signing")
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

java {
    withSourcesJar()
    withJavadocJar()
}

val centralToken = providers.environmentVariable("CENTRAL_TOKEN").orNull

publishing {
    repositories {
        maven {
            name = "localValidation"
            url = uri(layout.buildDirectory.dir("publishing-validation"))
        }
        if (!centralToken.isNullOrBlank()) {
            maven {
                name = "centralPortal"
                url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
                credentials(HttpHeaderCredentials::class) {
                    name = "Authorization"
                    value = "Bearer $centralToken"
                }
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
        }
    }

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
                developers {
                    developer {
                        id.set("skbindas")
                        name.set("Suhaib Choudhary")
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

val signingKey = providers.environmentVariable("MAVEN_CENTRAL_SIGNING_KEY").orNull
val signingPassword = providers.environmentVariable("MAVEN_CENTRAL_SIGNING_PASSWORD").orNull

signing {
    if (!signingKey.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["release"])
    }
}
