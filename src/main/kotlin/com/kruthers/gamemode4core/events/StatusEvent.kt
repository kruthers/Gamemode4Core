package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class StatusEvent(val plugin: Gamemode4Core): Listener {

    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        var motd: String = plugin.config.getString("server_config.motd") ?: ""
        motd = motd.replace("\n".toRegex(),"\n<newline>")
        var lines = motd.split("\n")
        if (lines.size < 2) {
            lines = lines.subList(0,1)
        }

        motd = ""
        lines.forEach { line -> motd+=line }

        val placeholders: TagResolver = TagResolver.resolver(
            Placeholder.parsed("online",Bukkit.getOnlinePlayers().toString()),
            Placeholder.parsed("max",plugin.config.getInt("server_config.max_players").toString())
        )

        event.motd(MiniMessage.miniMessage().deserialize(motd,placeholders))
    }

}