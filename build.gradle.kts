plugins {
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    group = "net.azisaba.rarity"
    version = "1.0.0-SNAPSHOT"

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

    publishing {
        repositories {
            maven {
                name = "repo"
                credentials(PasswordCredentials::class)
                url = uri(
                    if (project.version.toString().endsWith("SNAPSHOT"))
                        project.findProperty("deploySnapshotURL") ?: System.getProperty("deploySnapshotURL", "https://repo.azisaba.net/repository/maven-snapshots/")
                    else
                        project.findProperty("deployReleasesURL") ?: System.getProperty("deployReleasesURL", "https://repo.azisaba.net/repository/maven-releases/")
                )
            }
        }

        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }
    }

    repositories {
        mavenCentral()
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/") }
        maven { url = uri("https://repo.azisaba.net/repository/maven-public/") }
    }

    tasks {
        javadoc {
            options.encoding = "UTF-8"
        }

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
