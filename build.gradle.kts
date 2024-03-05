plugins {
    kotlin("jvm") version "1.9.21"
}

group = "com.oneeyedmen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.strikt:strikt-core:0.34.0")
    testImplementation("net.jqwik:jqwik-kotlin:1.8.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}