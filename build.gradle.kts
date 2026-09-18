plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
}

group = "me.neomines"
version = "1.0.0"
description = "NeoMines - Advanced Mines Management Plugin for Minecraft 1.21+"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.9") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.9") {
        isTransitive = false
    }
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("org.bstats:bstats-bukkit:3.1.0")
}

tasks {
    processResources {
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(
                "pluginName" to "NeoMines",
                "version" to project.version,
                "description" to project.description,
                "main" to "me.neomines.NeoMines"
            )
        }
    }

    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    shadowJar {
        dependsOn(processResources)
        archiveBaseName.set("NeoMines")
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())

        relocate("org.bstats", "me.neomines.shaded.metrics")
    }

    jar {
        enabled = false
    }

    build {
        dependsOn(shadowJar)
    }
}
