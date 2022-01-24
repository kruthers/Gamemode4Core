package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class StatusEvent(val plugin: Gamemode4Core): Listener {
    private val formattingRegex: Regex = "&[\\da-fk-or]".toRegex()
    private val motdLength: Int = 50;


    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        var motd: String = plugin.config.getString("server_config.motd") ?: ""
        var lines = motd.split("\n")
        if (lines.size < 2) {
            lines = lines.subList(0,1)
        }

        motd = ""

        lines.forEachIndexed { index: Int, Line: String ->
            var parsedString: String = Line.replace("{max}","${Bukkit.getServer().maxPlayers}")
            parsedString = parsedString.replace("{online}","${Bukkit.getServer().onlinePlayers.size}")

//            val noFormatting = Line.replace(this.formattingRegex,"")
//
//            if (noFormatting.length < this.motdLength) {
//                var spaces: Int = (this.motdLength-noFormatting.length)/2
//                while (spaces > 0) {
//                    parsedString = " $parsedString"
//                    spaces--
//                }
//
//            } else {
//                parsedString = parsedString.substring(0,this.motdLength)
//            }

            parsedString = ChatColor.translateAlternateColorCodes('&',parsedString)

            motd += parsedString

            if (index < 2) {
                motd += "\n${ChatColor.RESET}"
            }

        }

        event.motd = motd

    }

}