package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.modes.StreamerMode
import com.kruthers.gamemode4core.utils.loadPlayerData
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class StreamerModeCommand(val plugin:Gamemode4Core): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender;

            if (args.isEmpty()) {

                StreamerMode.toggle(plugin, player)

            } else {
                player.sendMessage("${ChatColor.RED}Invalid usage: /streammode")
            }
        } else {
            sender.sendMessage("${ChatColor.RED}Sorry this command can only be used by players")
        }

        return true
    }
}