package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class FreezeCommand(val plugin: Gamemode4Core): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (Gamemode4Core.playersFrozen) {
            sender.sendMessage("${ChatColor.RED}Everyone is already frozen, do /unfreeze to allow people to move again")
        } else {
            Gamemode4Core.playersFrozen = true;
            Bukkit.broadcastMessage(getMessage(plugin,"freeze.start_brodcast"))
            Bukkit.broadcast(getMessage(plugin,"freeze.staff_start").replace("{name}",sender.name),"gm4core.freeze.notify")
        }

        return true
    }
}