package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.modes.Watching
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WatchCommand(val plugin: Gamemode4Core): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender;

            if (args.size == 1) {
                val playerName: String = args[0]
                //check to make sure they are not watching themselves
                if (playerName != player.name) {
                    val target: Player? = Bukkit.getPlayer(playerName)

                    if (target == null) {
                        sender.sendMessage("${ChatColor.RED}Failed to find user $playerName")
                    } else {
                        Watching.enable(plugin,player,target)

                        Gamemode4Core.watchingPlayers[player] = target.uniqueId

                    }
                } else {
                    player.sendMessage("${ChatColor.RED}Sorry, you cannot watch yourself")
                }
            } else {
                player.sendMessage("${ChatColor.RED}Invalid usage: /watch <player>")
            }
        } else {
            sender.sendMessage("${ChatColor.RED}Sorry this command can only be used by players")
        }

        return true
    }
}