import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.3.20"
    id("org.jetbrains.intellij.platform") version "2.20.1"
}
repositories { mavenCentral(); intellijPlatform { defaultRepositories() } }
val catalogRuntime by configurations.creating
dependencies {
    catalogRuntime(kotlin("stdlib"))
    intellijPlatform {
        val sdk = providers.gradleProperty("idePath")
        if (sdk.isPresent) local(sdk.get()) else webstorm("2026.2.2")
        bundledPlugin("com.intellij.css")
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation(kotlin("test-junit"))
}
kotlin { jvmToolchain(25); compilerOptions { jvmTarget = JvmTarget.JVM_21 } }
intellijPlatform {
    pluginConfiguration { ideaVersion { sinceBuild = "262.10315.144"; untilBuild = "262.*" } }
    pluginVerification { ides {
        val sdk = providers.gradleProperty("idePath")
        if (sdk.isPresent) local(file(sdk.get()))
        else create(org.jetbrains.intellij.platform.gradle.IntelliJPlatformType.WebStorm, "2026.2.2")
    } }
}
tasks.register<JavaExec>("generateCatalog") {
    dependsOn("classes")
    classpath = sourceSets.main.get().runtimeClasspath + catalogRuntime
    mainClass = "dev.crossemmet.engine.CatalogReferenceKt"
    args(layout.projectDirectory.file("docs/abbreviations.md").asFile.absolutePath)
}
