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

private val mm = MiniMessage.miniMessage()

@Deprecated("Plugin arg no longer required", ReplaceWith("parse(string, tags)"))
fun parseString(string: String, plugin: Gamemode4Core, tags: TagResolver = TagResolver.empty()): Component =
    parse(string, tags)

fun parse(string: String, tags: TagResolver = TagResolver.empty()): Component {
    val plugin = Gamemode4Core.instance

    val placeholders: TagResolver = TagResolver.resolver(
        tags,
        Placeholder.parsed("online",Bukkit.getOnlinePlayers().toString()),
        Placeholder.parsed("max",plugin.config.getInt("server_config.max_players").toString()),
        Placeholder.parsed("prefix",plugin.config.getString("messages.prefix") ?: "<aqua>GM4<aqua>"),
        Placeholder.parsed("staff_prefix",plugin.config.getString("messages.staff_prefix") ?: "<gold>[STAFF]</gold>")
    )

    return mm.deserialize(string,placeholders)
}

@Deprecated("Plugin arg no longer required", ReplaceWith("parse(string, player, tags)"))
fun parseString(string: String, player: Player, plugin: Gamemode4Core, tags: TagResolver = TagResolver.empty()): Component =
    parse(string, player, tags)

fun parse(string: String, player: Player, tags: TagResolver = TagResolver.empty()): Component {

    val placeholders: TagResolver = TagResolver.resolver(
        tags,
        Placeholder.parsed("player",player.name),
        Placeholder.parsed("name",player.name),
        Placeholder.parsed("display_name",mm.serialize(player.displayName()))
    )

    val input = if (Gamemode4Core.placeholder) {
        PlaceholderAPI.setPlaceholders(player,string)
    } else string

    return parse(input, placeholders)
}

@Deprecated("Removed plugin argument", ReplaceWith("getMessage(location, tags)"))
fun getMessage(plugin: Gamemode4Core, location: String, tags: TagResolver = TagResolver.empty()): Component =
    getMessage(location, tags)


fun getMessage(location: String, tags: TagResolver = TagResolver.empty()): Component {
    val message: String? = Gamemode4Core.instance.config.getString("messages.$location")

    return if (message == null) {
        Component.text("Error in parsing message: ",NamedTextColor.RED)
            .append(Component.text(" Failed to find message messages:", NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text(location,NamedTextColor.GRAY,TextDecoration.ITALIC))
    } else {
        parse(message, tags)
    }
}

@Deprecated("Removed plugin argument", ReplaceWith("getMessage(location, player, tags)"))
fun getMessage(plugin: Gamemode4Core, location: String, player: Player, tags: TagResolver = TagResolver.empty()): Component =
    getMessage(location, player, tags)

fun getMessage(location: String, player: Player, tags: TagResolver = TagResolver.empty()): Component {
    val message: String? = Gamemode4Core.instance.config.getString("messages.$location")

    return if (message == null) {
        Component.text("Error in parsing message: ",NamedTextColor.RED)
            .append(Component.text(" Failed to find message messages:", NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text(location,NamedTextColor.GRAY,TextDecoration.ITALIC))
    } else {
        parse(message, player, tags)
    }
}