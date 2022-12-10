package com.kruthers.gamemode4core.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import com.kruthers.gamemode4core.utils.parseString
import org.bukkit.command.CommandSender

class CoreCommand(val plugin: Gamemode4Core) {

    @CommandMethod("gamemode4core|gm4core|gm4")
    @CommandPermission("gm4core.default")
    @CommandDescription("Core command for the plugin")
    fun onCoreCommand(sender: CommandSender) {
        sender.sendMessage(getMessage(plugin,"info"))
    }

    @CommandMethod("gamemode4core|gm4core|gm4 info")
    @CommandPermission("gm4core.default")
    @CommandDescription("Core command for the plugin")
    fun onInfoCommand(sender: CommandSender) {
        sender.sendMessage(getMessage(plugin,"info"))
    }

    @CommandMethod("gamemode4core|gm4core|gm4 reload")
    @CommandPermission("gm4core.reload")
    @CommandDescription("Reload the plugin plugin")
    fun onReloadCommand(sender: CommandSender) {
        plugin.reloadConfig()
        sender.sendMessage(parseString("<prefix> Reloaded GM4 Core Config",plugin))
    }

}