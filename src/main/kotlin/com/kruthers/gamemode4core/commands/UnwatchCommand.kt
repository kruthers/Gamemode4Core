package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.modes.Watching
import com.kruthers.gamemode4core.utils.loadPlayerData
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class UnwatchCommand(val plugin: Gamemode4Core): CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, lale: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender;

            if (args.isEmpty()) {
                var playerData: YamlConfiguration = loadPlayerData(plugin, player)

                if (playerData.getBoolean("mode.watching")) {

                    Watching.disable(plugin, player, playerData)

                } else {
                    player.sendMessage("${ChatColor.RED}You must be watching someone to be able to unwatch")
                }

            } else {
                player.sendMessage("${ChatColor.RED}Invalid usage: /unwatch")
            }

        } else {
            sender.sendMessage("${ChatColor.RED}You must be a player in watch mode to use this command")
        }

        return true
    }
}