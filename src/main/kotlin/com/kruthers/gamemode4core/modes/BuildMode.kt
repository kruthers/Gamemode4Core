package com.kruthers.gamemode4core.modes

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BuildMode {

    companion object {
        fun toggle(plugin: Gamemode4Core, player: Player) {
            var playerData: YamlConfiguration = loadPlayerData(plugin, player)
            val currentMode = playerData.getString("mode.current")

            when (currentMode) {
                "build" -> {
                    BuildMode.disable(plugin, player, playerData,true)
                }
                "watch" -> {
                    playerData = Watcher.disable(plugin, player, playerData, false)
                    BuildMode.enable(plugin, player, playerData,false)
                }
                else -> {
                    BuildMode.enable(plugin, player, playerData,true)
                }
            }
        }

        private fun enable(plugin: Gamemode4Core, player: Player, playerData: YamlConfiguration, saveSharedData: Boolean): YamlConfiguration {
            //set Mode data
            playerData.set("mode.current","build")

            //save shared data
            if (saveSharedData) {
                playerData.set("storage.shared.gamemode",player.gameMode.name)
                playerData.set("storage.shared.location",player.location)
            }

            //save buildmode data
            playerData.set("storage.build_mode.normal_data.xp.points",player.exp)
            playerData.set("storage.build_mode.normal_data.xp.levels",player.level)
            playerData.set("storage.build_mode.normal_data.inventory",player.inventory.contents)

            //handle dynmap
            if (Gamemode4Core.dynmap) {
                playerData.set("storage.build_mode.normal_data.dynmap_visibility",Gamemode4Core.dynmapAPI.getPlayerVisbility(player))
                Gamemode4Core.dynmapAPI.setPlayerVisiblity(player,false)
            }

            //Load in build mode inv, set gamemode and give them there perms
            player.inventory.clear()
            playerData.getList("storage.build_mode.build_data.inventory")?.forEachIndexed { index, it ->
                if (it is ItemStack) {
                    player.inventory.setItem(index,it)
                }
            }

            player.gameMode = GameMode.CREATIVE

            //give them the buildmode group in each world
            val group: String? = plugin.config.getString("buildmode.group")
            if (!playerAddGroup(player, group)) {
                plugin.logger.warning("Failed to add $group to ${player.name}, while entering build more, they will be missing extra perms")
                player.sendMessage("${ChatColor.RED}Failed to setup permissions for buildmode, please exit and try again, if this continues contact your system admin")
            }

            //add to boss bar
            Gamemode4Core.buildBossBar.addPlayer(player)

            //message
            player.sendMessage(getMessage(plugin,"buildmode.enter",player))

            //save data
            playerData.save(getPlayerDataFile(plugin, player))

            return playerData
        }

        fun disable(plugin: Gamemode4Core, player: Player, playerData: YamlConfiguration, resetSharedData: Boolean): YamlConfiguration {
            // save buildmode inventory
            playerData.set("mode.current","none")
            playerData.set("storage.build_mode.build_data.inventory",player.inventory.contents)

            //load the players build mode data back in
            player.exp = playerData.getDouble("storage.build_mode.normal_data.xp.points").toFloat()
            player.level = playerData.getInt("storage.build_mode.normal_data.xp.levels")

            //load shared data
            if (resetSharedData) {
                playerData.getLocation("storage.shared.location")?.let { player.teleport(it) }
                playerData.getString("storage.shared.gamemode")?.let { player.gameMode = GameMode.valueOf(it) }
            }

            //load there inv back in
            player.inventory.clear()
            playerData.getList("storage.build_mode.normal_data.inventory")?.forEachIndexed { index, it ->
                if (it is ItemStack) {
                    player.inventory.setItem(index,it)
                }
            }

            //handle dynmap
            if (Gamemode4Core.dynmap) {
                Gamemode4Core.dynmapAPI.setPlayerVisiblity(player,playerData.getBoolean("storage.build_mode.normal_data.dynmap_visibility"))
            }

            //remove permission group
            val group: String? = plugin.config.getString("buildmode.group")
            if (!playerRemoveGroup(player, group)) {
                plugin.logger.warning("Failed to remove $group from ${player.name}, while exiting build mode.")
                player.sendMessage("${ChatColor.RED}Failed to remove permissions for buildmode. This means the group has been removed already or has changed names, if this happens again please inform your system admin")
            }

            //remove from boss bar
            Gamemode4Core.buildBossBar.removePlayer(player)

            //send message
            player.sendMessage(getMessage(plugin,"buildmode.exit",player))

            //save data
            playerData.save(getPlayerDataFile(plugin, player))

            return playerData
        }
    }

}