package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.parseString
import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import java.awt.Color

class GriefEvents(val plugin: Gamemode4Core): Listener {
    private val warningTime: Long = 60*60*20
    private val blockedBlocks: MutableList<Material> = mutableListOf(Material.TNT,Material.FIRE,Material.LAVA,
        Material.WITHER_SKELETON_SKULL,Material.WITHER_SKELETON_WALL_SKULL)
    private val blockBrokenBlocks: MutableList<Material> = mutableListOf(Material.CHEST,Material.TRAPPED_CHEST,
        Material.BARREL,Material.HOPPER,Material.DISPENSER,Material.DROPPER,
        Material.CHEST_MINECART,Material.HOPPER_MINECART)

    private val kickMessages: MutableList<String> = mutableListOf(
        "Internal exception: java.io.IOException: Received string length longer than the maximum allowed (257>256)",
        "Internal Server Error",
        "Internal exception: java.io.IOException: An existing connection was forcibly closed by the remote host",
        "Internal exception: java.io.IOException: GM4 encountered an unknown error"
    )

    private fun checkPlayTime(player: Player, checkTime: Long = this.warningTime): Boolean {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE) < checkTime
    }

    private fun sendWarning(player: Player, warning: String, loc: Location) {
        val tags: TagResolver = TagResolver.resolver(
            Placeholder.unparsed("warning", warning),
            Placeholder.component(
                "location",
                Component.text("${loc.x} ${loc.y} ${loc.z}").decorate(TextDecoration.UNDERLINED).clickEvent(
                    ClickEvent.runCommand("/tpa l ${loc.x} ${loc.y} ${loc.z}")
                ).hoverEvent(HoverEvent.showText(
                    Component.text("Teleport to where the entry was logged")
                        .color(NamedTextColor.GRAY)
                        .decorate(TextDecoration.ITALIC)
                ))
            )
        )

        //send message to the server
        Bukkit.broadcast(
            parseString("<staff_prefix> <player> <red><warning> at <location>", player, plugin, tags),
            "gm4core.griefwarning"
        )
        //send discord message
        val logChannel: TextChannel? = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("grief-alerts")
        if (logChannel != null) {
            val embed = EmbedBuilder()
            embed.setTitle("Possible grief attempt detected")
            embed.setDescription("`${player.name}` $warning at ${loc.x} ${loc.y} ${loc.z}")
            embed.setColor(Color.RED)

            plugin.logger.info("$logChannel ${embed.build()}")

            logChannel.sendMessageEmbeds(embed.build()).queue()
        }

        //kick the player with a random message
        player.kick(Component.text(kickMessages.random()))
    }

    @EventHandler
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        if (checkPlayTime(event.player) && blockedBlocks.contains(event.block.type)) {
            event.isCancelled = true
            sendWarning(event.player,"tried to place down ${event.block.type.name}",event.block.location)
        }
    }

    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        // Block container breaking for first 20 minutes
        if (checkPlayTime(event.player, warningTime / 3) && blockBrokenBlocks.contains(event.block.type)) {
            event.isCancelled = true
            sendWarning(event.player, "tried to break ${event.block.type.name}",event.block.location)
        }
    }

    @EventHandler
    fun onEntityPlaceEvent(event: EntityPlaceEvent) {
        if (event.player == null) return

        val player: Player = event.player!!
        if (checkPlayTime(player)) {
            when (event.entityType) {
                EntityType.MINECART_TNT -> {
                    event.isCancelled = true
                    sendWarning(player,"Attempted to spawn a TNT minecart",event.entity.location)
                }
                EntityType.PRIMED_TNT -> {
                    event.isCancelled = true
                    sendWarning(player,"Attempted to ignite TNT",event.entity.location)
                }
                else -> {
                    return
                }
            }
        }
    }

    @EventHandler
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        if (checkPlayTime(event.player,warningTime/2) && event.bucket == Material.LAVA_BUCKET) {
            event.isCancelled = true
            sendWarning(event.player,"Attempted to place lava",event.block.location)
        }

    }
}