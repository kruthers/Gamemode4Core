package com.kruthers.gamemode4core.commands

import cloud.commandframework.annotations.*
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.modes.Watching
import com.kruthers.gamemode4core.utils.getMessage
import com.kruthers.gamemode4core.utils.loadPlayerData
import com.kruthers.gamemode4core.utils.parseString
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.util.*

class WatchCommands(val plugin: Gamemode4Core) {

    @ProxiedBy("owo")
    @CommandMethod("watch <player>")
    @CommandPermission("gm4core.mode.watch")
    @CommandDescription("Used to teleport to a player and go into spectator mode")
    fun onWatchCommand(player: Player, @Argument("player") target: Player) {
        if (player.name == target.name) {
            player.sendMessage(parseString("<prefix> <red>You are unable to watch yourself",plugin))
        }

        Watching.enable(plugin, player, target)
        Gamemode4Core.watchingPlayers[player] = target.uniqueId
    }

    @ProxiedBy("wc")
    @CommandMethod("watchconfirm")
    @CommandPermission("gm4core.mode.watch")
    @CommandDescription("Used to teleport to the currently watched player")
    fun onWatchConfirmCommand(player: Player) {
        val playerData: YamlConfiguration = loadPlayerData(plugin,player)

        if (playerData.getBoolean("mode.watching")) {
            val target: OfflinePlayer? = playerData.getString("storage.watching.target")?.let { Bukkit.getOfflinePlayer(
                UUID.fromString(it))
            }

            if (target == null) {
                Watching.disable(plugin, player, playerData, true)
                player.sendMessage(parseString("<prefix> <red>Unable to locate target disabling watch mode",plugin))
            } else {
                if (target.isOnline) {
                    target.player?.let { player.teleport(it.location) }
                } else {
                    player.sendMessage(getMessage(plugin,"watch.not_online",player))
                }
            }

        } else {
            player.sendMessage(getMessage(plugin,"watch.not_watching",player))
        }
    }

    @ProxiedBy("uwu")
    @CommandMethod("unwatch")
    @CommandPermission("gm4core.mode.watch")
    @CommandDescription("Unwatch a current player if watching them")
    fun onUnwatchCommand(player: Player) {
        val playerData: YamlConfiguration = loadPlayerData(plugin, player)
        if (playerData.getBoolean("mode.watching")) {
            Watching.disable(plugin, player, playerData, true)
        } else {
            player.sendMessage(getMessage(plugin,"watch.not_watching",player))
        }
    }

}