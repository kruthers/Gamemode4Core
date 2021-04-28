package com.kruthers.gamemode4core.utils

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.objects.Whitelist
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.*

fun initStorageFolders(plugin: Gamemode4Core): Boolean {
    val whitelistFolder: File = File("${plugin.dataFolder}/whitelists/")
    val playerDataFolder: File = File("${plugin.dataFolder}/player_data/")

    var sucesful: Boolean = true

    if (!playerDataFolder.exists()) {
        plugin.logger.info("Failed to find player data folder, creating a new one")
        sucesful = playerDataFolder.mkdir()
    }
    if (!whitelistFolder.exists()) {
        plugin.logger.info("Failed to find whielists folder, creating a new one")
        if (sucesful) {
            sucesful = whitelistFolder.mkdir();
        }
    }

    return sucesful
}

fun loadWhitelists(plugin: Gamemode4Core) {
    plugin.logger.info("Loading whitelists...")
    val folder: File = File("${plugin.dataFolder}/whitelists/")
    folder.listFiles()?.forEach {
        val whitelistFile: YamlConfiguration = YamlConfiguration.loadConfiguration(it)

        //parts
        val name: String = it.name.removeSuffix(".yml")
        plugin.logger.info("Found whitelist $name")
        val rejectionMsg: String = whitelistFile.getString("rejection_msg") ?: "";
        val uuids: MutableSet<UUID> = mutableSetOf()
        val parents: MutableSet<String> = mutableSetOf()

        whitelistFile.getStringList("players").forEach { uuid ->
            uuids.add(UUID.fromString(uuid))
        }

        whitelistFile.getStringList("parents").forEach { parent ->
            parents.add(parent)
        }

        val whitelist: Whitelist = Whitelist(name,uuids,parents,rejectionMsg)
        Gamemode4Core.whitelists[name] = whitelist

        if (whitelistFile.getBoolean("active")) {
            plugin.logger.info("Set active whitelist as $name")
            Gamemode4Core.activeWhitelist = name
        }

    }
    plugin.logger.info("Fully loaded whitelists")
}

fun loadPlayerData(plugin: Gamemode4Core, player: Player): YamlConfiguration {
    val dataFile = getPlayerDataFile(plugin,player)
    var playerData: YamlConfiguration;


    playerData = if (dataFile.exists()) {
        YamlConfiguration.loadConfiguration(dataFile);
    } else {
        generateBlankPlayerData()
    }


    return playerData
}

fun playerHasData(plugin: Gamemode4Core, player: Player): Boolean {
    val dataFile = getPlayerDataFile(plugin,player)

    return dataFile.exists()
}

fun getPlayerDataFile(plugin: Gamemode4Core, player: Player): File {
    return File("${plugin.dataFolder}/player_data/${player.uniqueId}.yml")
}


private fun generateBlankPlayerData(): YamlConfiguration {
    val blankData: YamlConfiguration = YamlConfiguration();

    //base data
    blankData.set("mode.current","none")
    blankData.set("mode.streamer",false)

    //base mode data
//    blankData.set("builddata.inventory",)

    return blankData;
}


