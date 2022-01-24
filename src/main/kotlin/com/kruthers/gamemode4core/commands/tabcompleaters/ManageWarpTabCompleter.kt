package com.kruthers.gamemode4core.commands.tabcompleaters

import com.kruthers.gamemode4core.commands.WarpCommand
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class ManageWarpTabCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        val argCount = args.size
        var result:MutableList<String> = mutableListOf()

        when(alias.toLowerCase()) {
            "removewarp" -> {
                if (argCount == 1) {
                    val start = args[0].toLowerCase()
                    result = WarpCommand.warps.keys.filter { it.toLowerCase().startsWith(start) }.toMutableList()
                } else if (args.isEmpty()) {
                    result = WarpCommand.warps.keys.toMutableList()
                }
            }
            "modifywarp", "managewarp" -> {
                when (argCount) {
                    0 -> {
                        result = mutableListOf("set","remove")
                    }
                    1 -> {
                        val start = args[0].toLowerCase()
                        result = mutableListOf("set","remove").filter { it.toLowerCase().startsWith(start) }.toMutableList()
                    }
                    2 -> {
                        val subCmd: String = args[0]
                        when (subCmd.toLowerCase()) {
                            "remove" -> {
                                val start = args[1].toLowerCase()
                                result = WarpCommand.warps.keys.filter { it.toLowerCase().startsWith(start) }.toMutableList()
                            }
                        }
                    }
                }
            }
        }

        return result
    }
}