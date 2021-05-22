package com.kruthers.gamemode4core.modes

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.*
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class StreamerMode {

    companion object {

        fun toggle(plugin: Gamemode4Core, player: Player) {
            var playerData: YamlConfiguration = loadPlayerData(plugin, player)

            playerData = if (playerData.getBoolean("mode.streamer")) {
                StreamerMode.disable(plugin, player, playerData)
            } else {
                StreamerMode.enable(plugin, player, playerData)
            }

            playerData.save(getPlayerDataFile(plugin, player))

        }


        private fun enable(plugin: Gamemode4Core, player: Player, playerData: YamlConfiguration): YamlConfiguration {

            val group: String? = plugin.config.getString("streamer_mode.group")
            if (Gamemode4Core.permission.groups.contains(group)) {
                playerData.set("mode.streamer",true)
                playerAddGroup(player, group)
                player.sendMessage(getMessage(plugin,"streammode.enter"))
            } else {
                player.sendMessage("${ChatColor.RED}Failed to find permissions group for notifications, unable to change mode. If this continues please inform you system admin")
                plugin.logger.warning("Failed to put ${player.name} into streamer mode: Failed to find group: $group")
            }

            return playerData
        }

        private fun disable(plugin: Gamemode4Core, player: Player, playerData: YamlConfiguration): YamlConfiguration {

            val group: String? = plugin.config.getString("streamer_mode.group")
            if (Gamemode4Core.permission.groups.contains(group)) {
                playerData.set("mode.streamer",false)
                playerRemoveGroup(player, group)
                player.sendMessage(getMessage(plugin,"streammode.exit"))
            } else {
                player.sendMessage("${ChatColor.RED}Failed to find permissions group for notifications, unable to change mode. If this continues please inform you system admin. (You are still in streamer mode)")
                plugin.logger.warning("Failed to exit ${player.name} from streamer mode: Failed to find group: $group")
            }

            return playerData;
        }
    }

}