package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.modes.Watcher
import com.kruthers.gamemode4core.utils.loadPlayerData
import com.kruthers.gamemode4core.utils.parseString
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class UnWatchCommand(val plugin: Gamemode4Core): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender;

            if (args.isEmpty()) {
                val playerData: YamlConfiguration = loadPlayerData(plugin, player)
                if (playerData.getString("mode.current") == "watch") {
                    Watcher.disable(plugin,player,playerData,true)
                } else {
                    player.sendMessage(parseString("{prefix} &cYou are not currently watching anyone, watch someone with /watch <player>",plugin))
                }

            } else {
                player.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /unwatch")
            }
        } else {
            sender.sendMessage("${ChatColor.RED}Sorry you must be a player to run this command")
        }

        return true;
    }
}