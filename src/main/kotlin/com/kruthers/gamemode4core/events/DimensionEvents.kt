package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class DimensionEvents(val plugin: Gamemode4Core): Listener {

    @EventHandler
    fun onDimensionChange(event: PlayerTeleportEvent) {
        if (event.from.world != event.to?.world) {
            val player: Player = event.player

            //if the player is being watched, inform the watcher
            if (Gamemode4Core.watchingPlayers.values.contains(player.uniqueId)) {
                Gamemode4Core.watchingPlayers.forEach { watcher, target ->
                    if (target == player.uniqueId) {
                        watcher.sendMessage(getMessage(plugin,"watch.dimentionChange",watcher).replace("{target}",player.name))
                    }
                }
            }

        }
    }

}