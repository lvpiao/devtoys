import org.jetbrains.intellij.platform.gradle.TestFrameworkType


plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "com.antgroup"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    gradlePluginPortal()
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        bundledPlugin("com.intellij.java")
        intellijIdeaCommunity("2023.3")
        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
    implementation("cn.hutool:hutool-all:5.8.32")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
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

    withType(JavaCompile::class.java) {
        options.encoding = "UTF-8"
    }

    withType<JavaExec> {
        systemProperty("file.encoding", "utf-8")
    }
}
