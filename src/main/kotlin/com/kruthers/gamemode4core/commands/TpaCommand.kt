package com.kruthers.gamemode4core.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.parseString
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

class TpaCommand(val plugin: Gamemode4Core) {

    private fun tpaPlayer(player: Player, location: Location) {
        //save their current locations
        plugin.addTPALocation(player)

        //create the location and tp them
        player.teleport(location)
    }

    @CommandMethod("tpa l <location> [dimension]")
    @CommandDescription("tpa to locations and remembers your to current location")
    @CommandPermission("gm4core.tpa")
    fun onTpaLocation(player: Player, @Argument("location") location: Location, @Argument("dimension") dimension: World?) {

        if (dimension != null) {
            location.world=dimension
        }

        tpaPlayer(player,location)

        player.sendMessage(
            parseString("<prefix> <green>Teleported to <italic>${location.x} ${location.y}" +
                    " ${location.z}</italic>, use /back to return to your previous location.", plugin
            )
        )
    }

    @CommandMethod("tpa p <player>")
    @CommandDescription("tpa to a player and remembers your to current location")
    @CommandPermission("gm4core.tpa")
    fun onTpaPlayer(player: Player, @Argument("player") target: Player) {

        val location = target.location

        tpaPlayer(player,location)

        player.sendMessage(
            parseString("<prefix> <green>Teleported to <italic>${target.name}</italic>, use /back to return " +
                    "to your previous location.", plugin
            )
        )
    }


}