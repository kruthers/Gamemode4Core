package com.kruthers.gamemode4core.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class FreezeCommand(val plugin: Gamemode4Core) {

    @CommandMethod("freeze")
    @CommandPermission("gm4core.freeze")
    @CommandDescription("Freeze everyone on the server")
    fun onFreezeCommand(sender: CommandSender) {
        if (Gamemode4Core.playersFrozen) {
            sender.sendMessage(Component.text("Everyone is already frozen, do /unfreeze to allow people to move again",NamedTextColor.RED))

        } else {
            Gamemode4Core.playersFrozen = true
            Bukkit.broadcast(getMessage(plugin,"freeze.start_brodcast"))
            Bukkit.broadcast(getMessage(plugin,"freeze.staff_start", TagResolver.resolver(
                Placeholder.unparsed("name",sender.name))),"gm4core.freeze.notify")
        }
    }

    @CommandMethod("unfreeze")
    @CommandPermission("gm4core.freeze")
    @CommandDescription("Unfreeze everyone")
    fun onUnfreezeCommand(sender: CommandSender) {
        if (!Gamemode4Core.playersFrozen) {
            sender.sendMessage(Component.text("Freeze is not active, do /freeze to change this",NamedTextColor.RED))
        } else {
            Gamemode4Core.playersFrozen = false
            Bukkit.broadcast(getMessage(plugin,"freeze.end_brodcast"))
            Bukkit.broadcast(getMessage(plugin,"freeze.staff_end", TagResolver.resolver(
                Placeholder.unparsed("name",sender.name))),"gm4core.freeze.notify")
        }
    }

}