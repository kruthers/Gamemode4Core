package com.kruthers.gamemode4core.utils

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.commands.WarpCommand
import com.kruthers.gamemode4core.events.PlayerConnectionEvents
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

const val CONFIG_VERSION = 3

fun initStorageFolders(plugin: Gamemode4Core): Boolean {
    val playerDataFolder = File("${plugin.dataFolder}/player_data/")

    var sucesful = true

    if (!playerDataFolder.exists()) {
        plugin.logger.info("Failed to find player data folder, creating a new one")
        sucesful = playerDataFolder.mkdir()
    }

    return sucesful
}


fun loadPlayerData(plugin: Gamemode4Core, player: Player): YamlConfiguration {
    val dataFile = getPlayerDataFile(plugin, player)


    return if (dataFile.exists()) {
        YamlConfiguration.loadConfiguration(dataFile)
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
    val blankData = YamlConfiguration()

    //base data
    blankData.set("mode.streamer",false)
    blankData.set("mode.mod_mode",false)
    blankData.set("mode.watching",false)

    return blankData
}

private fun updatePlayerDataFile(plugin: Gamemode4Core,playerDataFile: File) {
    val playerData: YamlConfiguration = YamlConfiguration.loadConfiguration(playerDataFile)

    val newPlayerData: YamlConfiguration = generateBlankPlayerData()

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
                val playerDataFolder = File("${plugin.dataFolder}/player_data/")

                playerDataFolder.listFiles()?.forEach { file ->
                    updatePlayerDataFile(plugin, file)
                }
            }
        }
    }

    plugin.config.set("config_version", CONFIG_VERSION)
    plugin.saveConfig()

}

fun loadWarps(plugin: Gamemode4Core) {
    val warpsFile = File("${plugin.dataFolder}/warps.yml")
    val warpsData = YamlConfiguration.loadConfiguration(warpsFile)

    val warps: Set<String> = warpsData.getConfigurationSection("warps")?.getKeys(false) ?: HashSet()
    for (warp in warps) {
        val location: Location? = warpsData.getLocation("warps.$warp")
        if (location!= null) {
            WarpCommand.warps[warp] = location
        }
    }

    saveWarps(plugin)

}

fun saveWarps(plugin: Gamemode4Core) {
    val warpsFile = File("${plugin.dataFolder}/warps.yml")
    val warpsData = YamlConfiguration.loadConfiguration(warpsFile)

    warpsData.set("warps",null)

    WarpCommand.warps.forEach { (warp, location) ->
        warpsData.set("warps.$warp",location)
    }

    warpsData.save(warpsFile)
}

