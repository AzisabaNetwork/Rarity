plugins {
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    apply {
        plugin("java")
        plugin("java-library")
        plugin("maven-publish")
        plugin("com.github.johnrengelman.shadow")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))

        withSourcesJar()
        withJavadocJar()
    }

    group = "net.azisaba.rarity"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/") }
        maven { url = uri("https://repo.acrylicstyle.xyz/repository/maven-public/") }
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }

        shadowJar {
            relocate("xyz.acrylicstyle.util", "net.azisaba.rarity.libs.xyz.acrylicstyle.util")
            relocate("org.mariadb.jdbc", "net.azisaba.rarity.libs.org.mariadb.jdbc")
            relocate("com.zaxxer.hikari", "net.azisaba.rarity.libs.com.zaxxer.hikari")
            archiveBaseName.set("Rarity-${project.name}")
        }
    }
}
