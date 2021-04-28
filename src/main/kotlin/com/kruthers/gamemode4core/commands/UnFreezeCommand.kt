package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class UnFreezeCommand(val plugin: Gamemode4Core): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!Gamemode4Core.playersFrozen) {
            sender.sendMessage("${ChatColor.RED}Everyone is already unfrozen, do /freeze to freeze everyone")
        } else {
            Gamemode4Core.playersFrozen = false;
            Bukkit.broadcastMessage(getMessage(plugin,"freeze.end_brodcast"))
            Bukkit.broadcast(getMessage(plugin,"freeze.staff_end").replace("{name}",sender.name),"gm4core.freeze.notify")
        }

        return true
    }
}