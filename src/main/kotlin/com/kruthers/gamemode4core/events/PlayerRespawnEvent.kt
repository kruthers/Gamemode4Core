package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawnEvent(val plugin: Gamemode4Core): Listener {

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        if (!event.isBedSpawn && !event.isAnchorSpawn) {
            event.respawnLocation = PlayerConnectionEvents.getConfigSpawn(plugin)
        }
    }

}