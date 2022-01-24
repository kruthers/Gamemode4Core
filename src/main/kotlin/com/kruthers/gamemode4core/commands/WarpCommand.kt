package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.loadPlayerData
import com.kruthers.gamemode4core.utils.parseString
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class WarpCommand(val plugin: Gamemode4Core): CommandExecutor {
    companion object {
        val warps: HashMap<String, Location> = HashMap()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when (args.size) {
            0 -> {
                sendWarpsList(sender)
            }
            1 -> {
                if (sender !is Player) {
                    sender.sendMessage("${ChatColor.RED}You must be a player to use this command")
                    return true
                }

                val arg = args[0].toLowerCase()
                if (arg == "list") {
                    sendWarpsList(sender)
                } else {
                    if (warps.containsKey(arg)) {
                        plugin.addTPALocation(sender)
                        warps[arg]?.let { sender.teleport(it) }
                        sender.sendMessage(parseString("{prefix} ${ChatColor.GREEN}Warped to '$arg', your previous location has been saved", plugin))
                    } else {
                        sender.sendMessage("${ChatColor.RED}Invalid warp provided '${arg}'")
                    }
                }
            }
            else -> {
                sender.sendMessage("${ChatColor.RED}Too many arguments provided, correct usage: /warp <warp>")
            }
        }

        return true
    }

    private fun sendWarpsList(sender: CommandSender) {
        val warpsText = TextComponent(parseString("{prefix} Current warps: ", plugin))
        warpsText.color = ChatColor.GREEN
        val warpsSize = warps.size
        var i = 0
        warps.forEach { (warp, _) ->
            val text = TextComponent(warp)
            text.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND,"/warp $warp")
            text.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Click to warp to $warp"))
            text.color = ChatColor.GOLD
            text.isUnderlined = true

            warpsText.addExtra(text)
            i++
            if (i < warpsSize) {
                warpsText.addExtra("${ChatColor.GREEN}, ")
            }
        }

        sender.spigot().sendMessage(warpsText)
    }
}