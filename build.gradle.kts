import java.io.File

println("==== GRADLE WORKING DIR: " + File(".").absolutePath)

plugins {
    kotlin("jvm") version "2.1.0"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.giabrend"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2025.1.2")
    type.set("IU")
    plugins.set(listOf())
}

tasks {
    buildSearchableOptions {
        enabled = false
    }
    patchPluginXml {
        sinceBuild.set("251")
        untilBuild.set("255.*")
    }
}

kotlin {
    jvmToolchain(17)
}
