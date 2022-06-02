plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.0"

}

group = "com.kruthers"
version = "2.4.2"
description = "The core plugin used to manage the gamemode 4 public server"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("net.kyori","adventure-platform-bukkit","4.0.1")

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    implementation("cloud.commandframework","cloud-core","1.6.2")
    implementation("cloud.commandframework","cloud-annotations","1.6.2")
    implementation("cloud.commandframework","cloud-paper","1.6.2")
    implementation("cloud.commandframework","cloud-minecraft-extras","1.6.2")

    compileOnly("me.clip","placeholderapi","2.11.1")
    compileOnly("net.luckperms","api","5.4")

}

tasks {
    shadowJar {
        destinationDirectory.set(file("build"))

        archiveClassifier.set("")

        dependencies {
            exclude(dependency("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT"))
            exclude(dependency("me.clip:placeholderapi:2.11.1"))
            exclude(dependency("net.luckperms:api:5.4"))
        }

        minimize()
    }
    build {
        dependsOn(shadowJar)

    }
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

