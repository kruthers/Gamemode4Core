package com.kruthers.gamemode4core.utils

import com.kruthers.gamemode4core.Gamemode4Core
import me.clip.placeholderapi.PlaceholderAPI
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.math.floor

fun parseString(string: String, plugin: Gamemode4Core): String {
    var parsed: String = string.replace("{online}", Bukkit.getOnlinePlayers().toString())
    parsed = parsed.replace("{max}", plugin.config.getInt("server_config.max_players").toString())

    parsed = parsed.replace("{prefix}",plugin.config.getString("messages.prefix").toString())


    parsed = ChatColor.translateAlternateColorCodes('&',parsed)
    return parsed
}

fun parsePlayerString(string: String, player: Player, plugin: Gamemode4Core): String {

    var parsed: String = string.replace("{player}", player.name)
    parsed = parsed.replace("{name}", player.name)

    parsed = parseString(parsed, plugin)

    if (Gamemode4Core.placeholder) {
        parsed = PlaceholderAPI.setPlaceholders(player,parsed)
    }

    return parsed
}

fun getMessage(plugin: Gamemode4Core, location: String): String {
    val message: String? = plugin.config.getString("messages.$location")

    return if (message == null) {
        "${ChatColor.RED}Error in parsing message: Failed to find message"
    } else {
        parseString(message,plugin)
    }
}

fun getMessage(plugin: Gamemode4Core, location: String, player: Player): String {
    val message: String? = plugin.config.getString("messages.$location")

    return if (message == null) {
        "${ChatColor.RED}Error in parsing message: Failed to find message"
    } else {
        parsePlayerString(message, player, plugin)
    }
}