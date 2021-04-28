package com.kruthers.gamemode4core.modes

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.util.*

class Watcher {

    companion object {

        fun enable(plugin: Gamemode4Core, player: Player, target: Player) {
            var playerData: YamlConfiguration = loadPlayerData(plugin, player)
            val currentMode = playerData.getString("mode.current")

            when (currentMode) {
                "build" -> {
                    playerData = BuildMode.disable(plugin, player, playerData,true)
                    Watcher.watch(plugin, player, playerData, false, target)

                }
                "watch" -> {
                    Watcher.watch(plugin, player, playerData, false, target)
                }
                else -> {
                    Watcher.watch(plugin, player, playerData, true, target)
                }
            }
        }

        private fun watch(plugin: Gamemode4Core, player: Player, playerData: YamlConfiguration, saveSharedData: Boolean, target: Player) {
            //set mode
            playerData.set("mode.current","watch")

            //save shared data
            if (saveSharedData) {
                playerData.set("storage.shared.gamemode",player.gameMode.name)
                playerData.set("storage.shared.location",player.location)
            }

            //handle dynmap
            if (Gamemode4Core.dynmap) {
                playerData.set("storage.watch.dynmap_visibility",Gamemode4Core.dynmapAPI.getPlayerVisbility(player))
                Gamemode4Core.dynmapAPI.setPlayerVisiblity(player,false)
            }

            //save data
            playerData.set("storage.watch.target",target.uniqueId.toString())

            //teleport to target & set gamemode
            player.gameMode = GameMode.SPECTATOR
            player.teleport(target)

            Gamemode4Core.watchingPlayers[player] = target.uniqueId

            //send message
            player.sendMessage(getMessage(plugin,"watch.start",player).replace("{target}",target.name))

            //save data
            playerData.save(getPlayerDataFile(plugin, player))

        }


        fun disable(plugin: Gamemode4Core, player: Player, playerData: YamlConfiguration, resetSharedData: Boolean): YamlConfiguration {
            //set mode
            playerData.set("mode.current","none")


            //load shared data & teleport
            if (resetSharedData) {
                playerData.getLocation("storage.shared.location")?.let { player.teleport(it) }
                playerData.getString("storage.shared.gamemode")?.let { player.gameMode = GameMode.valueOf(it) }
            }

            //handle dynmap
            if (Gamemode4Core.dynmap) {
                Gamemode4Core.dynmapAPI.setPlayerVisiblity(player,playerData.getBoolean("storage.watch.dynmap_visibility"))
            }

            //save data
            playerData.set("storage.watch.target",null)

            Gamemode4Core.watchingPlayers.remove(player)

            //send message
            player.sendMessage(getMessage(plugin,"watch.stop",player))

            //save data
            playerData.save(getPlayerDataFile(plugin, player))

            return playerData
        }

        fun teleport(plugin: Gamemode4Core, player: Player) {
            val uuid: String? = loadPlayerData(plugin, player).getString("storage.watch.target")
            if (uuid == null) {
                player.sendMessage(parseString("{prefix} &cYou are not currently watching anyone, watch someone with /watch <player>",plugin))
            } else {
                val target: OfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid))
                if (player.isOnline) {
                    target.player?.let { player.teleport(it) }
                } else {
                    player.sendMessage(parsePlayerString("{prefix} &cYour watch target is not currently online",player,plugin))
                }
            }
        }

    }

}