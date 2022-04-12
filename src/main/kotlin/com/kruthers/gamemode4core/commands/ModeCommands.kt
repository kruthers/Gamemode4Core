package com.kruthers.gamemode4core.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.ProxiedBy
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.modes.ModMode
import com.kruthers.gamemode4core.modes.StreamerMode
import org.bukkit.entity.Player

class ModeCommands(val plugin:Gamemode4Core) {

    @ProxiedBy("streamermode")
    @CommandMethod("streammode")
    @CommandPermission("gm4core.mode.streamer")
    @CommandDescription("Used to hide all staff notifcations")
    fun onStreamModeCommand(player: Player) {
        StreamerMode.toggle(plugin, player)
    }

    @CommandMethod("modmode")
    @CommandPermission("gm4core.mode.mod")
    @CommandDescription("Used to enter moderation mode and gain world edit perms")
    fun onModMoeCommand(player: Player) {
        ModMode.toggle(plugin, player)
    }

}