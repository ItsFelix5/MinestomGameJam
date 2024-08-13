plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "io.github.itsfelix5"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:461c56e749")
    implementation("dev.hollowcube:polar:1.11.1")
    implementation("ch.qos.logback:logback-core:1.5.6")
    implementation("ch.qos.logback:logback-classic:1.5.6")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "gamejam.Main"
    }
}