plugins {
    kotlin("jvm") version "2.4.10"
    id("com.vanniktech.maven.publish") version "0.36.0"
    kotlin("plugin.serialization") version "2.4.10"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "io.github.criticalay"
version = "1.2.1"

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
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")
    implementation("org.slf4j:slf4j-api:2.0.13")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.mockk:mockk:1.14.11")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

ktlint {
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    filter {
        exclude("**/build/**")
    }
}

// `.git` is a directory in a normal clone and a file in a worktree, so test for existence only.
val gitMetadata = rootProject.file(".git")
val runningOnCi = providers.environmentVariable("CI").isPresent

val installGitHooks by tasks.registering(Exec::class) {
    description = "Configures git core.hooksPath to .githooks (enables ktlint pre-commit hook)."
    group = "build setup"
    // Setting the same value twice is a no-op, so there is nothing to read back first.
    commandLine("git", "config", "core.hooksPath", ".githooks")
    isIgnoreExitValue = true
    onlyIf { gitMetadata.exists() && !runningOnCi }
}

tasks.named("ktlintCheck") {
    dependsOn(installGitHooks)
}
