package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.parseString
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent

class GriefEvents(val plugin: Gamemode4Core): Listener {
    private val warningTime: Int = 60*60*20
    private val blockedBlocks: MutableList<Material> = mutableListOf(Material.TNT,Material.FIRE,Material.LAVA,
        Material.WITHER_SKELETON_SKULL,Material.WITHER_SKELETON_WALL_SKULL)

    fun kickPlayer(player: Player) {
        player.kick(Component.text("An unknown GM4 exception occurred"))
    }

    @EventHandler
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        if (event.player.getStatistic(Statistic.PLAY_ONE_MINUTE) < warningTime) {
            if (blockedBlocks.contains(event.block.type)) {
                event.isCancelled = true

                val loc = event.block.location

                Bukkit.broadcast(parseString("<staff_prefix> <player> <red>tried to place down " +
                        "${event.block.type.name} at <u><click:suggest_command:'/tpa l ${loc.x} ${loc.y} ${loc.z}'>" +
                        "${loc.x} ${loc.y} ${loc.z}</click></u>", event.player, plugin))
                kickPlayer(event.player)
            }


        }
    }

    @EventHandler
    fun onEntityPlaceEvent(event: EntityPlaceEvent) {
        if (event.player == null) return

        val player: Player = event.player!!
        if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) < warningTime) {
            when (event.entityType) {
                EntityType.MINECART_TNT -> {
                    event.isCancelled = true
                    val loc = event.entity.location
                    Bukkit.broadcast(parseString("<staff_prefix> <player> <red>Attempted to spawn a TNT Minecart " +
                            "at <u><click:suggest_command:'/tpa l ${loc.x} ${loc.y} ${loc.z}'>" +
                            "${loc.x} ${loc.y} ${loc.z}</click></u>", player, plugin))
                    kickPlayer(player)
                }
                EntityType.PRIMED_TNT -> {
                    event.isCancelled = true
                    val loc = event.entity.location
                    Bukkit.broadcast(parseString("<staff_prefix> <player> <red>Attempted to ignite a TNT " +
                            "at <u><click:suggest_command:'/tpa l ${loc.x} ${loc.y} ${loc.z}'>" +
                            "${loc.x} ${loc.y} ${loc.z}</click></u>", player, plugin))
                    kickPlayer(player)
                }
                else -> {
                    return
                }
            }
        }
    }

    @EventHandler
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {

        if (event.player.getStatistic(Statistic.PLAY_ONE_MINUTE) < (warningTime/2)) {
            when (event.bucket) {
                Material.LAVA_BUCKET -> {
                    event.isCancelled = true
                    val loc = event.block.location
                    Bukkit.broadcast(parseString("<staff_prefix> <player> <red>Attempted to place lava " +
                            "at <u><click:suggest_command:'/tpa l ${loc.x} ${loc.y} ${loc.z}'>" +
                            "${loc.x} ${loc.y} ${loc.z}</click></u>", event.player, plugin))
                    kickPlayer(event.player)
                }
                else -> {
                    return
                }
            }
        }

    }
}