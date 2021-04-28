package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.parseString
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CoreCommand(val plugin:Gamemode4Core): CommandExecutor {
    private val mainMessage: String = "${ChatColor.AQUA}Welcome to Gamemode 4!\n"+
            "${ChatColor.GRAY}Gamemode 4 is a collection of datapacks that change and modify the gameplay experience\n"+
            "You can find out more on our website: https://gm4.co & on our wiki: https://wiki.gm4.co/wiki/Public_Server"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when(args.size) {
            0 -> {
                sender.sendMessage(mainMessage)
            }
            1 -> {
                when (args[0]) {
                    "rules" -> sender.sendMessage("${ChatColor.DARK_AQUA}[${ChatColor.AQUA}GM4${ChatColor.DARK_AQUA}] ${ChatColor.GRAY}You can read our servers' rules at https://gm4.co/rules")
                    "info" -> sender.sendMessage(mainMessage)
                    "reload" -> {
                        plugin.reloadConfig()
                        sender.sendMessage(parseString("{prefix} Reloaded GM4 Core Config & Whitelists",plugin))
                        //TODO Reload whitelists
                    }
                    else -> invalidArg(sender)
                }
            }
            else -> invalidArg(sender)
        }

        return true
    }

    private fun invalidArg(sender: CommandSender) {
        if (sender.hasPermission("gm4core.reload")) {
            sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /gm4core <reload|rules|info>")
        } else {
            sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /gm4core <rules|info>")
        }
    }
}