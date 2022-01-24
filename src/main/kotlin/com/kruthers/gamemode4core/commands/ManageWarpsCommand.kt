package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.parseString
import com.kruthers.gamemode4core.utils.saveWarps
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class ManageWarpsCommand(val plugin: Gamemode4Core): CommandExecutor {


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val argCount = args.size

        when(label.toLowerCase()) {
            "setwarp", "addwarp" -> {
                addWarpCommand(sender, label, args)
            }
            "removewarp" -> {
                if (argCount != 1) {
                    sender.sendMessage("${ChatColor.RED}Invalid amount of arguments provided, correct usage: /removewarp <warp> ")
                    return true
                }
                val warp: String = args[0].toLowerCase()
                removeWarp(warp,sender)
            }
            else -> {
                if (args.isNotEmpty()) {
                    val subCmd: String = args[0]
                    when (subCmd.toLowerCase()) {
                        "set", "add" -> {
                            val subLabel = "$label $subCmd"
                            val subArgs = args.copyOfRange(1, args.size)
                            addWarpCommand(sender, subLabel, subArgs)
                        }
                        "remove" -> {
                            if (argCount != 2) {
                                sender.sendMessage("${ChatColor.RED}Invalid amount of arguments provided, correct usage: /${label} remove <warp> ")
                                return true
                            }
                            val warp: String = args[1].toLowerCase()
                            removeWarp(warp,sender)
                        }
                        else -> {
                            sender.sendMessage("${ChatColor.RED}Invalid argument at /$label <--- Correct usage: /$label (set|remove) ...")
                        }
                    }
                } else {
                    sender.sendMessage("${ChatColor.RED}Expected argument at /$label <--- Correct usage: /$label (set|remove) ...")
                }
            }
        }
        return true
    }

    private fun addWarpCommand(sender: CommandSender, label: String, args: Array<out String>) {
        when (val argCount = args.size) {
            0 -> {
                sender.sendMessage("${ChatColor.RED}Expected argument at /$label <--- correct usage: /$label <name> [location]")
            }
            1 -> {
                if (sender is Player) {
                    val location:Location = sender.location
                    location.y = floor(location.y)
                    location.x = if (location.x > 0) floor(location.x)+0.5 else ceil(location.x)-0.5
                    location.z = if (location.z > 0) floor(location.z)+0.5 else ceil(location.z)-0.5
                    location.pitch = 0f
                    //calc yaw
                    var yaw: Float = location.yaw/45
                    yaw = round(yaw)
                    location.yaw = yaw*45

                    addWarp(args[0],location,sender)

                } else {
                    sender.sendMessage("${ChatColor.RED}You must be a player to execute this command, correct usage /$label <name> <location>")
                }
            }
            4, 6 -> {
                val name: String = args[0]
                var x = 0.0
                var y = 0.0
                var z = 0.0
                var pitch = 0f
                var yaw = 0f
                try {
                    x = args[1].toDouble()
                } catch (e: NumberFormatException) {
                    sender.sendMessage("${ChatColor.RED}Expected number at /$label $name <--- correct usage: /$label <name> [location]")
                }
                try {
                    y = args[2].toDouble()
                } catch (e: NumberFormatException) {
                    sender.sendMessage("${ChatColor.RED}Expected number at /$label $name $x <--- correct usage: /$label <name> [location]")
                }
                try {
                    z = args[3].toDouble()
                } catch (e: NumberFormatException) {
                    sender.sendMessage("${ChatColor.RED}Expected number at /$label $name $x $y <--- correct usage: /$label <name> [location]")
                }

                if (argCount == 6) {
                    try {
                        pitch = args[4].toFloat()
                    } catch (e: NumberFormatException) {
                        sender.sendMessage("${ChatColor.RED}Expected number at /$label $name $x $y $z <--- correct usage: /$label <name> [location]")
                    }
                    try {
                        yaw = args[4].toFloat()
                    } catch (e: NumberFormatException) {
                        sender.sendMessage("${ChatColor.RED}Expected number at /$label $name $x $y $z $pitch <--- correct usage: /$label <name> [location]")
                    }
                } else if (sender is Player) {
                    yaw = round(sender.location.yaw/45)*45
                }

                var world: World? = null
                if (sender is Player) {
                    world=sender.location.world
                }


                val location = Location(world, x, y, z, yaw, pitch)
                addWarp(name, location, sender)

            }
            else -> {
                sender.sendMessage("${ChatColor.RED}Invalid amount of arguments provided, correct usage: /$label <warp> <location>")
            }
        }
    }

    private fun addWarp(name: String, location: Location, sender: CommandSender) {
        if (!checkFormat(name)) {
            sender.sendMessage("${ChatColor.RED}Invalid warp name given, please only use a-z, 0-9, - or _")
            return
        }

        WarpCommand.warps[name.toLowerCase()] = location
        sender.sendMessage(parseString("{prefix} ${ChatColor.GREEN}Successfully added warp ${name.toLowerCase()}", plugin))
        saveWarps(this.plugin)
    }

    private fun removeWarp(name: String, sender: CommandSender) {
        if (WarpCommand.warps.containsKey(name)) {
            WarpCommand.warps.remove(name)
            sender.sendMessage(parseString("{prefix} ${ChatColor.GREEN}Removed warp '$name'", plugin))
        } else {
            sender.sendMessage("${ChatColor.RED}Invalid warp name '$name'")
        }
        saveWarps(this.plugin)
    }

    private fun checkFormat(name: String): Boolean {
        var result = name.matches("[\\w\\-]{3,}".toRegex())
        if (result) {
            result = (name.toLowerCase() != "list")
        }

        return result
    }


}