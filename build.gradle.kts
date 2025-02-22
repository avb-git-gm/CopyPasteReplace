plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.giabrend"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1") // JetBrains annotations
}

intellij {
    version.set("2023.1") // Adjust to your target version
    type.set("IC") // IntelliJ Community, change to IU for Ultimate
    plugins.set(listOf("com.intellij.java")) // Include required plugins
}

sourceSets {
    main {
        resources.srcDirs("src/main/resources") // ✅ Ensure resource folder is included
    }
}

// 🔹 Task to copy plugin.xml to the correct build location
val copyPluginXml by tasks.registering(Copy::class) {
    from("src/main/resources/META-INF/plugin.xml")
    into(layout.buildDirectory.dir("patchedPluginXmlFiles"))
}

// 🔹 Ensure processResources includes plugin.xml and icons
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/resources") {
        include("META-INF/plugin.xml", "**/*.svg") // ✅ Include plugin.xml & icons
    }
    into(layout.buildDirectory.dir("resources/main"))
    dependsOn(copyPluginXml) // ✅ Ensure plugin.xml is copied before processing
}

// 🔹 Ensure verifyPluginConfiguration runs after copying plugin.xml
tasks.named("verifyPluginConfiguration") {
    dependsOn(copyPluginXml)
}

// 🔹 Java & Kotlin Compiler Settings
tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("241") // ✅ Ensure correct sinceBuild to avoid null error
        untilBuild.set("243.*") // ✅ Support IntelliJ 2024 versions
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    // 🔹 Ensure `plugin.xml` is correctly detected
    withType<org.jetbrains.intellij.tasks.VerifyPluginConfigurationTask>().configureEach {
        pluginXmlFiles.set(fileTree("src/main/resources/META-INF").matching { include("plugin.xml") }.files)
    }
}

// 🔹 Fix Gradle duplicate resource issues
tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
