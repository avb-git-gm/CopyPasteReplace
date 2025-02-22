// Apply necessary Gradle plugins
plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4" // ✅ Stable version
}

group = "com.giabrend"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1") // ✅ JetBrains annotations
}

// ✅ IntelliJ Plugin Configuration
intellij {
    version.set("2024.3") // ✅ Matches IntelliJ IDEA 2024.3
    type.set("IC") // IntelliJ Community, use "IU" for Ultimate
    plugins.set(listOf("com.intellij.java")) // ✅ Include required plugins
}

// ✅ Ensure resource folder is included
sourceSets {
    main {
        resources.srcDirs("src/main/resources")
    }
}

// 🔹 Task to copy plugin.xml correctly
val copyPluginXml = tasks.register<Copy>("copyPluginXml") {
    from("src/main/resources/META-INF/plugin.xml")
    into(layout.buildDirectory.dir("patchedPluginXmlFiles"))
}

// 🔹 Ensure processResources includes plugin.xml and icons
tasks.withType<ProcessResources>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/resources") {
        include("META-INF/plugin.xml", "**/*.svg") // ✅ Ensure XML & icons are included
    }
    into(layout.buildDirectory.dir("resources/main"))
    dependsOn(copyPluginXml) // ✅ Ensures plugin.xml is copied before processing
}

// 🔹 Ensure verifyPluginConfiguration runs after copying plugin.xml
tasks.named<org.jetbrains.intellij.tasks.VerifyPluginConfigurationTask>("verifyPluginConfiguration") {
    dependsOn(copyPluginXml)
}

// 🔹 Java & Kotlin Compiler Settings
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "21"
}

// 🔹 Patch Plugin XML (Fixing Type Inference Issue)
tasks.named<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    sinceBuild.set("243") // ✅ Matches IntelliJ 2024.3
    untilBuild.set("244.*") // ✅ Supports future versions
}

// 🔹 Plugin Signing (Fixing Type Inference Issue)
tasks.named<org.jetbrains.intellij.tasks.SignPluginTask>("signPlugin") {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
}

// 🔹 Publish Plugin (Fixing Type Inference Issue)
tasks.named<org.jetbrains.intellij.tasks.PublishPluginTask>("publishPlugin") {
    token.set(System.getenv("PUBLISH_TOKEN"))
}

// 🔹 Ensure plugin.xml is properly detected
tasks.withType<org.jetbrains.intellij.tasks.VerifyPluginConfigurationTask>().configureEach {
    pluginXmlFiles.set(fileTree("src/main/resources/META-INF").matching { include("plugin.xml") }.files)
}
