package com.kruthers.gamemode4core.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.parseString
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.entity.Player

class BackCommand(val plugin: Gamemode4Core) {

    @CommandMethod("back [count]")
    @CommandDescription("Go back to your privous location")
    @CommandPermission("gm4core.tpa.back")
    fun onBackCommand(player: Player, @Argument("count") countInput: Int?) {
        val locations: MutableList<Location>? = Gamemode4Core.backLocations[player.uniqueId]
        var count: Int = countInput?: 1
        if (count < 1) {
            player.sendMessage(Component.text("Expected minimum count greater then 0, defaulted to 1",NamedTextColor.RED))
            count = 1
        }

        if (locations == null || locations.isEmpty()) {
            player.sendMessage("<prefix> <red>You have no where to return too")
        } else {
            //get the location to return to
            val location: Location = if (count > locations.size) {
                locations[locations.size-1]
            } else {
                locations[count-1]
            }

            player.teleport(location)
            val remaining: Int = removeLocations(player,count)
            player.sendMessage(parseString("<prefix> <red>Returned to the location, you have $remaining locations left",plugin))
        }

    }

    private fun removeLocations(player: Player, count: Int): Int {
        var locations: MutableList<Location> = Gamemode4Core.backLocations[player.uniqueId] ?: mutableListOf()

        val remaining: Int = locations.size - count
        if (remaining < 1) {
            Gamemode4Core.backLocations.remove(player.uniqueId)
            return 0
        } else {
            locations = locations.subList(count,locations.size)
            Gamemode4Core.backLocations[player.uniqueId] = locations
        }

        return remaining
    }
}