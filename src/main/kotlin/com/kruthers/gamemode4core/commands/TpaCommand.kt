package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.loadPlayerData
import com.kruthers.gamemode4core.utils.parseString
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class TpaCommand(val plugin: Gamemode4Core): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender

            when (args.size) {
                1 -> {
                    val playerName: String = args[0]
                    val target: Player? = Bukkit.getPlayer(playerName)
                    if (target != null) {
                        plugin.addTPALocation(player)
                        player.teleport(target)
                        sender.sendMessage(parseString("{prefix} ${ChatColor.GREEN}Successfully teleported to $playerName do /back to return to your last location", plugin))
                    } else {
                        sender.sendMessage("${ChatColor.RED}Unable to find player $playerName")
                    }
                }
                3, 4 -> {
                    val x: Double;
                    val y: Double;
                    val z: Double;
                    val world: World?;

                    //args
                    val arg1: String = args[0]
                    val arg2: String = args[1]
                    val arg3: String = args[2]

                    //get and validate x
                    x = try {
                        arg1.toDouble()
                    } catch (e: NumberFormatException) {
                        if (arg1.startsWith("~")) {
                            try {
                                arg1.replace("~", "").toDouble()
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("${ChatColor.RED}Invalid number given at /tpa ${arg1}... Must be a double or number. Correct usage: /tpa <x> <y> <z> [world]")
                                return true
                            }
                        } else {
                            sender.sendMessage("${ChatColor.RED}Invalid number given at /tpa $arg1... Must be a double or number. Correct usage: /tpa <x> <y> <z> [world]")
                            return true
                        }
                    }

                    //get and validate y
                    y = try {
                        arg2.toDouble()
                    } catch (e: NumberFormatException) {
                        if (arg1.startsWith("~")) {
                            try {
                                arg2.replace("~", "").toDouble()
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("${ChatColor.RED}Invalid number given at /tpa $arg1 $arg2... Must be a double or number. Correct usage: /tpa <x> <y> <z> [world]")
                                return true
                            }
                        } else {
                            sender.sendMessage("${ChatColor.RED}Invalid number given at /tpa $arg1 $arg2 $arg3... Must be a double or number. Correct usage: /tpa <x> <y> <z> [world]")
                            return true
                        }
                    }

                    //get and validate z
                    z = try {
                        arg3.toDouble()
                    } catch (e: NumberFormatException) {
                        if (arg3.startsWith("~")) {
                            try {
                                arg3.replace("~", "").toDouble()
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("${ChatColor.RED}Invalid number given at /tpa $arg1 $arg2 $arg3... Must be a double or number. Correct usage: /tpa <x> <y> <z> [world]")
                                return true
                            }
                        } else {
                            sender.sendMessage("${ChatColor.RED}Invalid number given at /tpa $arg1 $arg2 $arg3... Must be a double or number. Correct usage: /tpa <x> <y> <z> [world]")
                            return true
                        }
                    }

                    //get the world, if its 4 args they should have provided it
                    world = if (args.size == 3) {
                        player.location.world
                    } else {
                        Bukkit.getServer().getWorld(args[3])
                    }

                    //valid that they gave a valid world
                    if (world == null) {
                        sender.sendMessage("${ChatColor.RED}Inbalid world given at /tpa ${args[0]} ${args[1]} ${args[2]} ${args[4]} <- Must be a valid world name")
                        return true
                    }

                    //save their current locations
                    plugin.addTPALocation(player)

                    //create the location and tp them
                    val location: Location = Location(world,x, y, z)
                    player.teleport(location)
                    player.sendMessage("${ChatColor.GREEN}Successfully teleported to X:$x, Y:$y Z:$z in ${world.name} do /back to return to your last location. \n${ChatColor.GRAY}You have ${getLocations(player)}/${plugin.config.getInt("stored_locations.back")} locations stored")
                }
                else -> player.sendMessage("${ChatColor.RED}Invalid Arguments, correct usage: either ${ChatColor.DARK_RED}/tpa <player> ${ChatColor.RED} or ${ChatColor.DARK_RED}/tpa <x> <y> <z> [world]")
            }
        } else {
            sender.sendMessage("${ChatColor.RED}You must be a player to run this command")
        }

        return true
    }

    private fun getLocations(player: Player): Int {
        val locations: MutableList<Location> = Gamemode4Core.backLocations[player.uniqueId] ?: mutableListOf()

        return locations.size
    }
}