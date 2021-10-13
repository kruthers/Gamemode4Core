package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.TimeSkipEvent

class TimeEvents(val plugin: Gamemode4Core): Listener {

    @EventHandler
    fun onTimeSkip(event: TimeSkipEvent) {
//        Bukkit.broadcastMessage("Time was skipped in ${event.world.name} for ${event.skipAmount} ticks because of ${event.skipReason}. ${event.world.name}'s time is at ${event.world.time}")

        if (plugin.config.getBoolean("dim_sleep_bug.fix") && event.skipReason == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            if (plugin.config.getStringList("dim_sleep_bug.no_sleep_skip").contains(event.world.name)) {
                event.isCancelled = true
            } else {
                val worldNames = plugin.config.getStringList("dim_sleep_bug.keep_time_in_sync")
                val time: Long = event.world.time+event.skipAmount
                worldNames.forEach { worldName ->
                    val world: World? = Bukkit.getWorld(worldName)
                    if (world != null && worldName != event.world.name) {
                        world.time = time
                    }
                }
            }
        }
    }

}