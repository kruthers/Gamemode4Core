package com.kruthers.gamemode4core.utils

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.events.PlayerConnectionEvents
import com.kruthers.gamemode4core.objects.Whitelist
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*

val CONFIG_VERSION = 3;

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
    val dataFile = getPlayerDataFile(plugin, player)


    return if (dataFile.exists()) {
        YamlConfiguration.loadConfiguration(dataFile);
    } else {
        generateBlankPlayerData()
    }
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
    blankData.set("mode.streamer",false)
    blankData.set("mode.mod_mode",false)
    blankData.set("mode.watching",false)

    //base mode data
//    blankData.set("builddata.inventory",)

    return blankData;
}

private fun updatePlayerDataFile(plugin: Gamemode4Core,playerDataFile: File) {
    val playerData: YamlConfiguration = YamlConfiguration.loadConfiguration(playerDataFile)

    val newPlayerData: YamlConfiguration = generateBlankPlayerData();

    if (playerData.getString("mode.current") == "build") {
        //old shared data
        val gamemode: String = playerData.getString("storage.shared.gamemode")?:"SURVIVAL"
        val location: Location = try {
            playerData.getLocation("storage.shared.location")?:PlayerConnectionEvents.getConfigSpawn(plugin)
        } catch (e: Exception) {
            plugin.logger.warning("Invalid location for returning to")
            PlayerConnectionEvents.getConfigSpawn(plugin)
        }

        //old build mode data
        val inv = playerData.getList("storage.build_mode.normal_data.inventory")
        val xpPoints: Double = playerData.getDouble("storage.build_mode.normal_data.xp.points")
        val xpLevels: Int = playerData.getInt("storage.build_mode.normal_data.xp.levels")

        newPlayerData.set("mode.mod_mode", true)

        newPlayerData.set("storage.mod_mode.normal_data.xp.points", xpPoints)
        newPlayerData.set("storage.mod_mode.normal_data.xp.levels", xpLevels)
        newPlayerData.set("storage.mod_mode.normal_data.inventory", inv)
        newPlayerData.set("storage.mod_mode.normal_data.gamemode", gamemode)
        newPlayerData.set("storage.mod_mode.normal_data.location", location)

    }

    newPlayerData.set("mode.streamer",playerData.getBoolean("mode.streamer"))

    newPlayerData.save(playerDataFile)
    plugin.logger.info("Updated $playerDataFile")
}

fun configVersionCheck(plugin: Gamemode4Core,config: FileConfiguration) {
    val version: Int = config.getInt("config_version")
    if (File("${plugin.dataFolder}/config.yml").exists()) {
        if (version < CONFIG_VERSION) {
            plugin.logger.warning("Outdated config version detected: $version")
            if (version < 1) {
                plugin.logger.warning("Found an outdated config file, updating")
                val playerDataFolder: File = File("${plugin.dataFolder}/player_data/")
                val files = playerDataFolder.listFiles();

                files.forEach { file ->
                    updatePlayerDataFile(plugin,file)
                }
            }
        }
    }

    plugin.config.set("config_version", CONFIG_VERSION)
    plugin.saveConfig()


}


