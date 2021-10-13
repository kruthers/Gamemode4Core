package com.kruthers.gamemode4core.modes

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.*
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ModMode {

    companion object {
        fun toggle(plugin: Gamemode4Core, player: Player) {
            var playerData: YamlConfiguration = loadPlayerData(plugin, player)
            val inMode: Boolean = playerData.getBoolean("mode.mod_mode")

            if (inMode) {
                ModMode.disable(plugin,player,playerData)
            } else {
                ModMode.enable(plugin,player,playerData)
            }
        }

        fun enable(plugin: Gamemode4Core, player: Player, playerData: YamlConfiguration ): YamlConfiguration {
            //set Mode data
            playerData.set("mode.mod_mode",true)

            //save mode data
            playerData.set("storage.mod_mode.normal_data.xp.points",player.exp)
            playerData.set("storage.mod_mode.normal_data.xp.levels",player.level)
            playerData.set("storage.mod_mode.normal_data.inventory",player.inventory.contents)
            playerData.set("storage.mod_mode.normal_data.gamemode",player.gameMode.name)
            playerData.set("storage.mod_mode.normal_data.location",player.location)

            //save tpa locations
            // TODO

            //handle dynmap
            if (Gamemode4Core.dynmap) {
                playerData.set("storage.mod_mode.normal_data.dynmap_visibility",Gamemode4Core.dynmapAPI.getPlayerVisbility(player))
                Gamemode4Core.dynmapAPI.setPlayerVisiblity(player,false)
            }

            //Load in mode inv, set gamemode and give them there perms
            player.inventory.clear()
            playerData.getList("storage.mod_mode.build_data.inventory")?.forEachIndexed { index, it ->
                if (it is ItemStack) {
                    player.inventory.setItem(index,it)
                }
            }

            player.gameMode = GameMode.CREATIVE

            //give them the mode group in each world
            val group: String? = plugin.config.getString("mod_mode.group")
            if (!playerAddGroup(player, group)) {
                plugin.logger.warning("Failed to add $group to ${player.name}, while entering mod mode, they will be missing extra perms")
                player.sendMessage("${ChatColor.RED}Failed to setup permissions for mod mode, please exit and try again, if this continues contact your system admin")
            }

            //add to boss bar
            Gamemode4Core.modModeBossBar.addPlayer(player)

            //message
            player.sendMessage(getMessage(plugin,"mod_mode.enter",player))

            //save data
            playerData.save(getPlayerDataFile(plugin, player))

            return playerData
        }

        fun disable(plugin: Gamemode4Core, player: Player, pd: YamlConfiguration): YamlConfiguration {
            var playerData: YamlConfiguration = pd
            //disabling watching
            if (playerData.getBoolean("mode.watching")){
                playerData = Watching.disable(plugin, player, playerData)
            }

            // save mode inventory
            playerData.set("mode.mod_mode",false)
            playerData.set("storage.mod_mode.build_data.inventory",player.inventory.contents)

            //load the players build mode data back in
            player.exp = playerData.getDouble("storage.mod_mode.normal_data.xp.points").toFloat()
            player.level = playerData.getInt("storage.mod_mode.normal_data.xp.levels")

            playerData.getLocation("storage.mod_mode.normal_data.location")?.let { player.teleport(it) }
            playerData.getString("storage.mod_mode.normal_data.gamemode")?.let { player.gameMode = GameMode.valueOf(it) }

            //load there inv back in
            player.inventory.clear()
            playerData.getList("storage.mod_mode.normal_data.inventory")?.forEachIndexed { index, it ->
                if (it is ItemStack) {
                    player.inventory.setItem(index,it)
                }
            }

            //handle dynmap
            if (Gamemode4Core.dynmap) {
                Gamemode4Core.dynmapAPI.setPlayerVisiblity(player,playerData.getBoolean("storage.mod_mode.normal_data.dynmap_visibility"))
            }

            //remove permission group
            val group: String? = plugin.config.getString("mod_mode.group")
            if (!playerRemoveGroup(player, group)) {
                plugin.logger.warning("Failed to remove $group from ${player.name}, while exiting mod mode.")
                player.sendMessage("${ChatColor.RED}Failed to remove permissions for mod mode. This means the group has been removed already or has changed names, if this happens again please inform your system admin")
            }

            //remove from boss bar
            Gamemode4Core.modModeBossBar.removePlayer(player)

            //send message
            player.sendMessage(getMessage(plugin,"mod_mode.exit",player))

            //save data
            playerData.save(getPlayerDataFile(plugin, player))

            return playerData
        }
    }

}