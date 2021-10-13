package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.modes.Watching
import com.kruthers.gamemode4core.utils.loadPlayerData
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.util.*

class WatchConfirmCommand(val plugin: Gamemode4Core): CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, lable: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (args.isEmpty()) {
                val player: Player = sender;

                val playerData: YamlConfiguration = loadPlayerData(plugin,player)

                if (playerData.getBoolean("mode.watching")) {

                    val target: OfflinePlayer? = playerData.getString("storage.watching.target")?.let { Bukkit.getOfflinePlayer(
                        UUID.fromString(it))
                    }

                    if (target == null) {
                        player.sendMessage("${ChatColor.RED}Unable to locate target, exiting watch mode")
                        Watching.disable(plugin, player, playerData)
                    } else {
                        if (target.isOnline) {
                            target.player?.let { player.teleport(it.location) }
                        } else {
                            player.sendMessage("${ChatColor.RED}Your target is not currently online")
                        }
                    }

                } else {
                    player.sendMessage("${ChatColor.RED}You must be watching someone to run this command")
                }

            } else {
                sender.sendMessage("${ChatColor.RED}Invalid usage: /watchconfirm")
            }
        } else {
            sender.sendMessage("${ChatColor.RED}Sorry this command can only be used by players")
        }

        return true
    }
}