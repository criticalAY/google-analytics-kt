plugins {
    kotlin("jvm") version "2.3.10"
}

group = "com.criticalay"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
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