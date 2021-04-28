package com.kruthers.gamemode4core.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ComingSoon: CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        sender.sendMessage("${ChatColor.RED}This command is comming soon")
        return true
    }
}