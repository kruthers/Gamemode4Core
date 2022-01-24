package com.kruthers.gamemode4core.commands.tabcompleaters

import com.kruthers.gamemode4core.commands.WarpCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class WarpTabCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        var result:MutableList<String> = mutableListOf()
        if (alias != "warps") {
            if (args.size == 1) {
                val start = args[0].toLowerCase()
                result = WarpCommand.warps.keys.filter { it.toLowerCase().startsWith(start) }.toMutableList()
            } else if (args.isEmpty()) {
                result = WarpCommand.warps.keys.toMutableList()
            }
        }
        return result
    }
}