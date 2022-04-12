package com.kruthers.gamemode4core.utils

import com.kruthers.gamemode4core.Gamemode4Core
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.entity.Player

val mm = MiniMessage.miniMessage()

fun parseString(string: String, plugin: Gamemode4Core, tags: TagResolver = TagResolver.empty()): Component {

    val placeholders: TagResolver = TagResolver.resolver(
        tags,
        Placeholder.parsed("online",Bukkit.getOnlinePlayers().toString()),
        Placeholder.parsed("max",plugin.config.getInt("server_config.max_players").toString()),
        Placeholder.parsed("prefix",plugin.config.getString("messages.prefix") ?: "<aqua>GM4<aqua>"),
        Placeholder.parsed("staff_prefix",plugin.config.getString("messages.staff_prefix") ?: "<gold>[STAFF]</gold>")
    )

    return mm.deserialize(string,placeholders)
}

fun parseString(string: String, player: Player, plugin: Gamemode4Core, tags: TagResolver = TagResolver.empty()): Component {

    val placeholders: TagResolver = TagResolver.resolver(
        tags,
        Placeholder.parsed("player",player.name),
        Placeholder.parsed("name",player.name),
        Placeholder.parsed("display_name",mm.serialize(player.displayName()))
    )

    var input = string

    if (Gamemode4Core.placeholder) {
        input = PlaceholderAPI.setPlaceholders(player,input)
    }

    return parseString(input, plugin, placeholders)
}

fun getMessage(plugin: Gamemode4Core, location: String, tags: TagResolver = TagResolver.empty()): Component {
    val message: String? = plugin.config.getString("messages.$location")

    return if (message == null) {
        Component.text("Error in parsing message: ",NamedTextColor.RED)
            .append(Component.text(" Failed to find message messages:", NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text(location,NamedTextColor.GRAY,TextDecoration.ITALIC))
    } else {
        parseString(message,plugin, tags)
    }
}

fun getMessage(plugin: Gamemode4Core, location: String, player: Player, tags: TagResolver = TagResolver.empty()): Component {
    val message: String? = plugin.config.getString("messages.$location")

    return if (message == null) {
        Component.text("Error in parsing message: ",NamedTextColor.RED)
            .append(Component.text(" Failed to find message messages:", NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text(location,NamedTextColor.GRAY,TextDecoration.ITALIC))
    } else {
        parseString(message, player, plugin, tags)
    }
}