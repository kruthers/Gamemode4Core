package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import com.kruthers.gamemode4core.utils.loadPlayerData
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.lang.NumberFormatException
import java.util.*

class BackCommand(val plugin: Gamemode4Core): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender

            val playerData: YamlConfiguration = loadPlayerData(plugin, player)
            if (playerData.getString("mode.current") != "none") {
                player.sendMessage("${ChatColor.RED}Sorry you cant use tpa when in a mode currently, try using build mode and normal tping")
                return true
            }

            when (args.size) {
                0 -> {
                    var locations: MutableList<Location>? = Gamemode4Core.backLocations[player.uniqueId]
                    if (locations == null) {
                        player.sendMessage("${ChatColor.RED}You have no where to return too")
                    } else {
                        val location: Location = locations[0]

                        player.teleport(location)
                        val remaining: Int = removeLocations(player,1)
                        player.sendMessage("${ChatColor.AQUA}Returned to your last location, you have $remaining locations left")
                    }
                }
                1 -> {
                    var locations: MutableList<Location>? = Gamemode4Core.backLocations[player.uniqueId]
                    if (locations == null) {
                        player.sendMessage("${ChatColor.RED}You have no where to return too")
                    } else {
                        val steps: Int

                        try {
                            steps = args[0].toInt()
                        } catch (e: NumberFormatException) {
                            player.sendMessage("${ChatColor.RED}Invalid number given expected /back <count>")
                            return true
                        }

                        //get the locations
                        val location: Location = if (steps > locations.size) {
                            locations[locations.size-1]
                        } else {
                            locations[steps-1]
                        }

                        player.teleport(location)
                        val remaining: Int = removeLocations(player,steps)

                        player.sendMessage("${ChatColor.AQUA}Returned to the location, you have $remaining locations left")

                    }
                }
                else -> player.sendMessage("${ChatColor.RED}Too many arguments given, correct usage: /back <count>")
            }

        } else {
            sender.sendMessage("${ChatColor.RED}You must be a player to run this command")
        }

        return true
    }

    private fun removeLocations(player: Player, count: Int): Int {
        var locations: MutableList<Location> = Gamemode4Core.backLocations[player.uniqueId] ?: mutableListOf()


        val remaining: Int = locations.size - count
        if (remaining < 1) {
            Gamemode4Core.backLocations.remove(player.uniqueId)
            return 0
        } else {
            locations = locations.subList(count,locations.size)
            Gamemode4Core.backLocations[player.uniqueId] = locations
        }

        return remaining
    }
}