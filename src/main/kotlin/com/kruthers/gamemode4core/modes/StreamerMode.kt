package com.kruthers.gamemode4core.modes

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class StreamerMode {

    companion object {

        fun toggle(plugin: Gamemode4Core, player: Player) {
            val playerData: YamlConfiguration = loadPlayerData(plugin, player)
            val inMode: Boolean = playerData.getBoolean("mode.streamer")

           if (inMode) {
                disable(plugin, player, playerData)
            } else {
                enable(plugin, player, playerData)
            }
        }


        private fun enable(plugin: Gamemode4Core, player: Player, playerData: YamlConfiguration): YamlConfiguration {

            val group: String? = plugin.config.getString("streamer_mode.group")

            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                if (playerAddGroup(player, group)) {
                    playerData.set("mode.streamer",true)
                    player.sendMessage(getMessage(plugin,"streammode.enter"))
                }else {
                    player.sendMessage(Component.text("Failed to find permissions group for notifications, unable " +
                            "to change mode. If this continues please inform you system admin", NamedTextColor.RED))
                    plugin.logger.warning("Failed to put ${player.name} into streamer mode: Failed to find group: $group")
                }

                playerData.save(getPlayerDataFile(plugin, player))
            })

            return playerData
        }

        private fun disable(plugin: Gamemode4Core, player: Player, playerData: YamlConfiguration): YamlConfiguration {

            val group: String? = plugin.config.getString("streamer_mode.group")

            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                if(playerRemoveGroup(player, group)) {
                    playerData.set("mode.streamer",false)
                    player.sendMessage(getMessage(plugin,"streammode.exit"))
                } else {
                    player.sendMessage(Component.text("Failed to update permissions group for notifications, " +
                            "unable to change mode. If this continues please inform you system admin. " +
                            "(You are still in streamer mode)", NamedTextColor.RED))
                    plugin.logger.warning("Failed to exit ${player.name} from streamer mode: Failed to find group: $group")
                }

                playerData.save(getPlayerDataFile(plugin, player))
            })

            return playerData
        }
    }

}