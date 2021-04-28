package com.kruthers.gamemode4core.commands.tabcompleaters

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class CoreCommandTabCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        var options: MutableList<String> = mutableListOf()
        when (args.size) {
            0 -> {
                options = mutableListOf("rules","info")
                if (sender.hasPermission("gm4core.reload")) options.add("reload")
            }
            1 -> {
                val arg: String = args[0].toLowerCase()
                if ("rules".contains(arg)) {
                    options.add("rules")
                }
                if ("info".contains(arg)) {
                    options.add("info")
                }
                if ("reload".contains(arg) && sender.hasPermission("gm4core.reload")) {
                    options.add("reload")
                }
            }
        }

        return options
    }
}