package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPortalEvent
import org.bukkit.event.player.PlayerTeleportEvent

class DimensionEvents(val plugin: Gamemode4Core): Listener {

    @EventHandler
    fun onDimensionChange(event: PlayerTeleportEvent) {
        if (event.from.world != event.to.world) {
            val player: Player = event.player

            //if the player is being watched, inform the watcher
            if (Gamemode4Core.watchingPlayers.values.contains(player.uniqueId)) {
                Gamemode4Core.watchingPlayers.forEach { watcher, target ->
                    if (target == player.uniqueId) {
                        watcher.sendMessage(getMessage(plugin,"watch.dimension_change",watcher,
                            TagResolver.resolver(Placeholder.unparsed("target",player.name))))
                    }
                }
            }

        }
    }

    //Item Teleportation
    @EventHandler
    fun onEntityDimensionChange(event: EntityPortalEvent) {
        if (event.from.world?.environment == World.Environment.THE_END) {
            event.to = PlayerConnectionEvents.getConfigSpawn(plugin)
        }
    }

}