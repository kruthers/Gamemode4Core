package com.kruthers.gamemode4core.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.ProxiedBy
import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.context.CommandContext
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.parseString
import com.kruthers.gamemode4core.utils.saveWarps
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class ManageWarpsCommand(val plugin: Gamemode4Core) {

    @ProxiedBy("addwarp")
    @CommandMethod("managewarps add <name> [location]")
    @CommandDescription("Add a new warp")
    @CommandPermission("gm4core.warp.manage")
    fun onWarpAddCommand(sender: CommandSender, @Argument("name",parserName = "warpNameAdd") name: String, @Argument("location") loc: Location?) {
        var location: Location? = loc
        if (location == null) {
            if (sender !is Player) {
                throw IllegalArgumentException("You must either provide a location or run this command as a player")
            } else {
                location = sender.location

                //centralize location
                location.y = floor(location.y)
                location.x = if (location.x > 0) floor(location.x) +0.5 else ceil(location.x) -0.5
                location.z = if (location.z > 0) floor(location.z) +0.5 else ceil(location.z) -0.5

                // set pitch to 0
                location.pitch = 0f

                //calc yaw
                var yaw: Float = location.yaw/45
                yaw = round(yaw)
                location.yaw = yaw*45
            }
        }

        WarpCommand.warps[name.lowercase()] = location
        sender.sendMessage(parseString("<prefix> <green>Successfully added warp ${name.lowercase()} <red>", plugin))
        saveWarps(this.plugin)
    }

    @ProxiedBy("removewarp")
    @CommandMethod("managewarps remove <name>")
    @CommandDescription("Remove a warp")
    @CommandPermission("gm4core.warp.manage")
    fun onWarpRemoveCommand(sender: CommandSender, @Argument("name",parserName = "warp") warp: Pair<String, Location>) {
        val name: String = warp.first

        if (WarpCommand.warps.containsKey(name)) {
            WarpCommand.warps.remove(name)
            sender.sendMessage(parseString("<prefix> <green>Removed warp '$name' </green>", plugin))
        } else {
            sender.sendMessage(Component.text("Invalid warp name '$name'",NamedTextColor.RED))
        }
        saveWarps(this.plugin)
    }

    @ProxiedBy("warps")
    @CommandMethod("managewarps lists")
    @CommandDescription("List all warps")
    @CommandPermission("gm4core.warp")
    fun onWarpListCommand(sender: CommandSender) {
        WarpCommand.sendWarpsList(sender,plugin)
    }


    @Parser(name = "warpNameAdd")
    fun warpNameParser(sender: CommandContext<CommandSender>, inputQueue: Queue<String>): String {
        val input = inputQueue.remove()

        if (input.matches("[\\w\\-]{3,}".toRegex()) && input.lowercase() != "list") {
            return  input
        } else {
            throw IllegalArgumentException("Invalid warp name given, please only use a-z, 0-9, - or _")
        }

    }

}