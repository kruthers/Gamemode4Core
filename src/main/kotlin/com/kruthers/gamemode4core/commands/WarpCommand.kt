package com.kruthers.gamemode4core.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.parsers.Parser
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.parseString
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class WarpCommand(val plugin: Gamemode4Core) {
    companion object {
        val warps: HashMap<String, Location> = HashMap()

        fun sendWarpsList(sender: CommandSender, plugin: Gamemode4Core) {
            var warpsText = parseString("<prefix> <green>Current warps: </green>",plugin)
            val warpsSize = warps.size
            var i = 0
            warps.forEach { (warp, _) ->
                val text = MiniMessage.miniMessage().deserialize(" <hover:show_text:'<dark_purple>Teleport to $warp" +
                        "</dark_purple>'><click:run_command:'/warp $warp'><gold>$warp</gold></click></hover>")

                warpsText = warpsText.append(text)
                i++
                if (i < warpsSize) {
                    warpsText.append(Component.text(",", NamedTextColor.GREEN))
                }
            }

            sender.sendMessage(warpsText)
        }
    }

    @CommandMethod("warp <warp>")
    @CommandDescription("warp to a location")
    @CommandPermission("gm4core.warp")
    fun onWarpCommand(player: Player, @Argument("warp", parserName = "warp") warp: Pair<String,Location>) {
        if (warp .first == "list") {
            sendWarpsList(player, plugin)
        } else {
            plugin.addTPALocation(player)
            player.teleport(warp.second)
            player.sendMessage(parseString("<prefix> <green>Warped to '${warp.first}', your previous location has been saved", plugin))
        }
    }


    @Suggestions("warp")
    fun coreArgsSuggester(sender: CommandContext<CommandSender>, input: String): List<String> {
        val response: MutableList<String> = mutableListOf()

        warps.forEach { (warp) ->
            if (warp.startsWith(input)) {
                response.add(warp)
            }
        }
        if ("list".startsWith(input)) {
            response.add("list")
        }

        return response.toList()
    }

    @Parser(name = "warp", suggestions = "warp")
    fun coreArgsParser(sender: CommandContext<CommandSender>, inputQueue: Queue<String>): Pair<String,Location> {
        val input = inputQueue.remove().lowercase()
        if (input == "list") {
            return Pair("list", Location(Bukkit.getWorlds()[0],0.0,0.0,0.0))
        }

        warps.forEach { (warp, loc) ->
            if (warp == input) {
                return Pair(warp,loc)
            }
        }

        throw IllegalArgumentException("Warp $input not recognised, unable to warp")
    }

}