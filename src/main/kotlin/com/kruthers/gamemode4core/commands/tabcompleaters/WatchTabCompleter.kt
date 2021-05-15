package com.kruthers.gamemode4core.commands.tabcompleaters

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class WatchTabCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val options: MutableList<String> = mutableListOf()
        if (args.isEmpty()) {
            Bukkit.getOnlinePlayers().forEach {
                if (it != sender) {
                    options.add(it.name)
                }
            }
        } else if (args.size == 1) {
            val arg: String = args[0]
            Bukkit.getOnlinePlayers().forEach {
                if (it.name != sender.name) {
                    val name: String = it.name
                    if (name.toLowerCase().contains(arg.toLowerCase())) {
                        options.add(name)
                    }
                }
            }
        }

        return options
    }
}