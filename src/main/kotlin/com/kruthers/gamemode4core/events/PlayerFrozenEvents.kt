package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent

class PlayerFrozenEvents(val plugin: Gamemode4Core): Listener {

    //Player moving
    @EventHandler
    public fun onPlayerMove(event: PlayerMoveEvent) {

        if (Gamemode4Core.playersFrozen && !event.player.hasPermission("gm4core.freeze.byspass")) {
            val from: Location = event.from;
            val to: Location? = event.to;

            if (to != null) {
                if (from.x != to.x || from.z != to.z) {
                    to.x = from.x
                    to.z = from.z
                    event.player.sendMessage(getMessage(plugin, "freeze.fozen"))
                }
            }
        }

    }

    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity is Player && Gamemode4Core.playersFrozen) {
            event.isCancelled = true
        }

    }


}