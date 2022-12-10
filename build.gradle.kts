plugins {
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"

}

group = "com.kruthers"
version = "2.5.2"
description = "The core plugin used to manage the gamemode 4 public server"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://nexus.scarsz.me/content/groups/public/") }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("net.kyori:adventure-api:4.11.0")

    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")

    implementation("cloud.commandframework","cloud-core","1.7.1")
    implementation("cloud.commandframework","cloud-annotations","1.7.1")
    implementation("cloud.commandframework","cloud-paper","1.7.1")
    implementation("cloud.commandframework","cloud-minecraft-extras","1.7.1")

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
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

