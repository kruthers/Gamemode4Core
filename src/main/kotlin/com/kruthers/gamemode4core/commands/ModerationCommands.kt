package com.kruthers.gamemode4core.commands

import cloud.commandframework.CommandManager
import cloud.commandframework.annotations.*
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument
import cloud.commandframework.context.CommandContext
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.parse
import com.kruthers.gamemode4core.utils.parseString
import me.confuser.banmanager.common.api.BmAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun registerModerationCommands(manager: CommandManager<CommandSender>) {
    val base = manager.commandBuilder("warnings", { "Display current warnings" }, "warns")

    manager.command(base
        .senderType(Player::class.java)
        .permission("gm4core.warnings")
        .handler(::listOwnWarnings)
    ).command(base
        .argument(OfflinePlayerArgument.of("player"))
        .permission("gm4core.warnings.others")
        .handler(::listOtherWarnings)
    )
}

private fun listOwnWarnings(ctx: CommandContext<CommandSender>) =
    checkPlayer(ctx.sender as Player, ctx.sender)

private fun listOtherWarnings(ctx: CommandContext<CommandSender>) =
    checkPlayer(ctx.get("player"), ctx.sender)

private fun checkPlayer(check: OfflinePlayer, requester: CommandSender) {
    val playerData = BmAPI.getPlayer(check.uniqueId)
    val warnings = BmAPI.getWarnings(playerData)

    var response: Component = parse("<green>Active warnings for ${check.name}</green>")
        .appendNewline()

    if (warnings.hasNext()) {
        while (warnings.hasNext()) {
            val warning = warnings.next()
            response = response.append(parse("<grey><italic>${warning.reason} </italic></grey><gold>(${warning.points}) Warned by ${warning.actor.name}</gold>"))
            if (warnings.hasNext()) response =response.appendNewline()
        }
    } else {
        response =response.append(parse("<red>This person has no active warnings</red>"))
    }

    requester.sendMessage(response)
}