import org.jetbrains.intellij.platform.gradle.TestFrameworkType


plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "com.antgroup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    gradlePluginPortal()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        bundledPlugin("com.intellij.java")
        intellijIdeaCommunity("2024.2")
        pluginVerifier()
        zipSigner()
        instrumentationTools()

        testFramework(TestFrameworkType.Platform)
    }

}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
