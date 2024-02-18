package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPortalEvent
import org.bukkit.event.player.PlayerTeleportEvent

class DimensionEvents(val plugin: Gamemode4Core): Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDimensionChange(event: PlayerTeleportEvent) {
        if (event.from.world != event.to.world) {
            val player: Player = event.player

            // if the player is being watched, inform the watcher
            Gamemode4Core.watchingPlayers.forEach { (watcher, target) ->
                if (target != player.uniqueId) { return@forEach }
                watcher.spectatorTarget?.let {
                    watcher.spectatorTarget = null
                    watcher.teleport(event.to)
                    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                        watcher.spectatorTarget = player
                    }, 4L)
                }

                watcher.sendMessage(getMessage(plugin,"watch.dimension_change",watcher,
                    TagResolver.resolver(Placeholder.unparsed("target",player.name))))
            }
        }
    }

    // Item Teleportation
    @EventHandler
    fun onEntityDimensionChange(event: EntityPortalEvent) {
        if (event.from.world?.environment == World.Environment.THE_END) {
            event.to = PlayerConnectionEvents.getConfigSpawn(plugin)
        }
    }

}