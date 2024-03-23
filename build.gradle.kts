plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = "com.kruthers"
version = "2.8.0"
description = "The core plugin used to manage the gamemode 4 public server"

repositories {
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://nexus.scarsz.me/content/groups/public/")
}

dependencies {
    compileOnly(kotlin("stdlib"))

    compileOnly("net.kyori:adventure-api:4.15.0")

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    val cloudVersion = "1.8.4"
    compileOnly("cloud.commandframework","cloud-core",cloudVersion)
    compileOnly("cloud.commandframework","cloud-annotations",cloudVersion)
    compileOnly("cloud.commandframework","cloud-paper",cloudVersion)
    compileOnly("cloud.commandframework","cloud-minecraft-extras",cloudVersion)

    compileOnly("me.clip","placeholderapi","2.11.1")
    compileOnly("net.luckperms","api","5.4")
    compileOnly("me.confuser.banmanager:BanManagerCommon:7.9.0")
    compileOnly("com.discordsrv:discordsrv:1.26.0")

}

tasks {
    shadowJar {
        destinationDirectory.set(file("build"))

        archiveClassifier.set("")

        dependencies {
            exclude(dependency("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT"))
            exclude(dependency("me.clip:placeholderapi:2.11.1"))
            exclude(dependency("net.luckperms:api:5.4"))
            exclude(dependency("me.confuser.banmanager:BanManagerCommon:7.9.0"))
            exclude(dependency("com.discordsrv:discordsrv:1.26.0"))
        }

        minimize()
    }
    build {
        dependsOn(shadowJar)
    }
    processResources {
        expand("name" to project.name, "description" to project.description, "version" to project.version)
    }
    runServer {
        dependsOn("build")
        minecraftVersion("1.20.4")
    }
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

