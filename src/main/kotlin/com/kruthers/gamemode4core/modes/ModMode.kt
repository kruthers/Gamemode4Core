package com.kruthers.gamemode4core.modes

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect

class ModMode {

    companion object {
        fun toggle(plugin: Gamemode4Core, player: Player) {
            val playerData: YamlConfiguration = loadPlayerData(plugin, player)
            val inMode: Boolean = playerData.getBoolean("mode.mod_mode")

            if (inMode) {
                disable(plugin,player,playerData)
            } else {
                enable(plugin,player,playerData)
            }
        }

        fun enable(plugin: Gamemode4Core, player: Player, playerData: YamlConfiguration ): YamlConfiguration {
            //if they have tpa'ed, return them and clear there locations
            val storedLocations = Gamemode4Core.backLocations[player.uniqueId]
            if (storedLocations != null) {
                if (!storedLocations.isEmpty()) {
                    player.teleport(storedLocations[storedLocations.size-1])
                    Gamemode4Core.backLocations[player.uniqueId] = mutableListOf()
                }
            }

            //set Mode data
            playerData.set("mode.mod_mode",true)

            //save mode data
            playerData.set("storage.mod_mode.normal_data.xp.points",player.exp)
            playerData.set("storage.mod_mode.normal_data.xp.levels",player.level)
            playerData.set("storage.mod_mode.normal_data.inventory",player.inventory.contents)
            playerData.set("storage.mod_mode.normal_data.gamemode",player.gameMode.name)
            playerData.set("storage.mod_mode.normal_data.location",player.location)
            playerData.set("storage.mod_mode.normal_data.effects",player.activePotionEffects)

            //Load in mode inv, set gamemode and give them there perms
            player.inventory.clear()
            playerData.getList("storage.mod_mode.build_data.inventory")?.forEachIndexed { index, it ->
                if (it is ItemStack) {
                    player.inventory.setItem(index,it)
                }
            }

            // Clear potion effect
            for (effect in player.activePotionEffects) {
                player.removePotionEffect(effect.type)
            }

            // Load their build mode effects
            playerData.getList("storage.mod_mode.build_data.effects")?.forEachIndexed { index, it ->
                if (it is PotionEffect)
                    player.addPotionEffect(it)
            }


            player.gameMode = GameMode.CREATIVE

            //give them the mode group in each world
            val group: String? = plugin.config.getString("mod_mode.group")
            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                if (!playerAddGroup(player, group)) {
                    plugin.logger.warning("Failed to add $group to ${player.name}, while entering mod mode, they will be missing extra perms")
                    player.sendMessage(Component.text("Failed to setup permissions for mod mode, please exit and try again, if this continues contact your system admin", NamedTextColor.RED))
                }
            })

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
                playerData = Watching.disable(plugin, player, playerData, false)
            }

            // save mode inventory
            playerData.set("mode.mod_mode",false)
            playerData.set("storage.mod_mode.build_data.inventory",player.inventory.contents)
            playerData.set("storage.mod_mode.build_data.effects",player.activePotionEffects)

            //load the players build mode data back in
            player.exp = playerData.getDouble("storage.mod_mode.normal_data.xp.points").toFloat()
            player.level = playerData.getInt("storage.mod_mode.normal_data.xp.levels")

            playerData.getLocation("storage.mod_mode.normal_data.location")?.let { player.teleport(it) }
            playerData.getString("storage.mod_mode.normal_data.gamemode")?.let { player.gameMode = GameMode.valueOf(it) }

            //load their inv back in
            player.inventory.clear()
            playerData.getList("storage.mod_mode.normal_data.inventory")?.forEachIndexed { index, it ->
                if (it is ItemStack) {
                    player.inventory.setItem(index,it)
                }
            }

            //if they have tpa, clear there locations
            val storedLocations = Gamemode4Core.backLocations[player.uniqueId]
            if (storedLocations != null) {
                if (!storedLocations.isEmpty()) {
                    Gamemode4Core.backLocations[player.uniqueId] = mutableListOf()
                }
            }

            // Clear potion effects
            for (effect in player.activePotionEffects) {
                player.removePotionEffect(effect.type)
            }

            // revert potion effects
            playerData.getList("storage.mod_mode.normal_data.effects")?.forEachIndexed { index, it ->
                if (it is PotionEffect)
                player.addPotionEffect(it)
            }

            //remove permission group
            val group: String? = plugin.config.getString("mod_mode.group")
            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                if (!playerRemoveGroup(player, group)) {
                    plugin.logger.warning("Failed to remove $group from ${player.name}, while exiting mod mode.")
                    player.sendMessage(Component.text("Failed to remove permissions for mod mode. This means the group has been removed already or has changed names, if this happens again please inform your system admin", NamedTextColor.RED))
                }
            })

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