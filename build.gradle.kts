plugins {
    kotlin("jvm") version "2.3.10"
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.github.criticalay"
version = "1.0.0"

repositories {
    mavenCentral()
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    pom {
        name.set("google-analytics-kt")
        description.set("SDKless analytics library")
        url.set("https://github.com/criticalAY/google-analytics-kt")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("criticalay")
                name.set("criticalay")
                email.set("criticalay@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/criticalAY/google-analytics-kt.git")
            developerConnection.set("scm:git:ssh://github.com/criticalAY/google-analytics-kt.git")
            url.set("https://github.com/criticalAY/google-analytics-kt")
        }
    }
}

dependencies {

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
    implementation("org.slf4j:slf4j-api:2.0.13")


    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.mockk:mockk:1.13.8")
}

kotlin {
    jvmToolchain(22)
}

tasks.test {
    useJUnitPlatform()
}