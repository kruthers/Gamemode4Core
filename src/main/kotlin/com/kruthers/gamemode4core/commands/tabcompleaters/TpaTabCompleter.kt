package com.kruthers.gamemode4core.commands.tabcompleaters

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TpaTabCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val options: MutableList<String> = mutableListOf()

        if (sender is Player) {
            val player: Player = sender
            when (args.size) {
                0 -> {
                    Bukkit.getOnlinePlayers().forEach {
                        if (player != it) {
                            options.add(player.name)
                        }
                    }

                    options.add("~ ~ ~")
                }
                1 -> {
                    val arg: String = args[0]
                    Bukkit.getOnlinePlayers().forEach {
                        if (player != it && player.name.toLowerCase().contains(arg.toLowerCase())) {
                            options.add(player.name)
                        }
                    }

                    if ("~".contains(arg)) {
                        options.add("~ ~ ~")
                    }
                }
                2 -> {
                    val arg: String = args[1]
                    if ("~".contains(arg)) {
                        options.add("~ ~")
                    }
                }
                3 -> {
                    val arg: String = args[2]
                    if ("~".contains(arg)) {
                        options.add("~")
                    }
                }
                4 -> {
                    val arg: String = args[3]
                    Bukkit.getWorlds().forEach {
                        if (it.name.toLowerCase().contains(arg.toLowerCase())) {
                            options.add(it.name)
                        }
                    }

                }
            }

        }

        return options
    }
}