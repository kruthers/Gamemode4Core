package com.kruthers.gamemode4core.commands

import cloud.commandframework.annotations.*
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.parseString
import me.confuser.banmanager.common.api.BmAPI
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ModerationCommands( val plugin: Gamemode4Core) {

    @ProxiedBy("warns")
    @CommandMethod("warnings [player]")
    @CommandPermission("gm4core.warnings")
    @CommandDescription("Display current warnings")
    fun onWarningsCommand(sender: CommandSender, @Argument("player", suggestions = "warnedPlayers") player: Player?) {
        var checkPlayer = player
        if (!sender.hasPermission("gm4core.warnings.others") || checkPlayer == null) {
            if (sender is Player) {
                checkPlayer = sender
            } else {
                throw Exception("You must be a player to run this command")
            }
        }

        val playerData = BmAPI.getPlayer(checkPlayer.uniqueId)
        val warnings = BmAPI.getWarnings(playerData)

        val response: Component = parseString("<green>Active warnings for ${checkPlayer.name}</green>", plugin)

        if (warnings.hasNext()) {
            while (warnings.hasNext()) {
                val warning = warnings.next()
                response.append(parseString("<grey><italic>${warning.reason} </italic></grey><gold>(${warning.points}) Warned by ${warning.actor.name}</gold>", plugin))
                if (warnings.hasNext()) response.append(Component.text("\n"))
            }
        } else {
            response.append(parseString("<red>This person has no active warnings</red>", plugin))
        }

        sender.sendMessage(response)
    }

    @Suggestions("warnedPlayers")
    fun warnedPlayersSuggestor(sender: CommandContext<CommandSender>, input: String): List<String> {
        return if (sender.hasPermission("gm4core.warnings.others")) {
            Bukkit.getOnlinePlayers().mapNotNull { it.player?.name }.filter { it.startsWith(input, true) }
        } else {
            mutableListOf()
        }
    }
}