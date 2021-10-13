package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.modes.ModMode
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ModModeCommand(val plugin: Gamemode4Core): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender;

            if (args.isEmpty()) {
                ModMode.toggle(plugin, player)

            } else {
                player.sendMessage("${ChatColor.RED}Invalid usage: /buildmode")
            }
        } else {
            sender.sendMessage("${ChatColor.RED}Sorry this command can only be used by players")
        }

        return true
    }
}