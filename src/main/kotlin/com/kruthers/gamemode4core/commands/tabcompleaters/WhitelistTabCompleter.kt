package com.kruthers.gamemode4core.commands.tabcompleaters

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.objects.Whitelist
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.jetbrains.annotations.Nullable

class WhitelistTabCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        var options: MutableList<String> = mutableListOf()
        val baseOptions: MutableList<String> = mutableListOf("add","remove","create","modify","info","delete","set")

        when (args.size) {
            0 -> {
                options = baseOptions
            }
            1 -> {
                val arg: String = args[0]
                baseOptions.forEach{
                    if (it.contains(arg)) {
                        options.add(it)
                    }
                }
            }
            2 -> {
                val subCmd: String = args[0]
                if (subCmd != "create") {
                    val arg: String = args[1]
                    Gamemode4Core.whitelists.forEach { (t, _) ->
                        if (t.contains(arg)) {
                            options.add(t)
                        }
                    }
                }
            }
            3 -> {
                val arg: String = args[2]
                when (args[0]) {
                    "add" -> {
                        Bukkit.getOnlinePlayers().forEach {
                            val name: String = it.name
                            if (name.contains(arg)) {
                                options.add(name)
                            }
                        }
                    }
                    "remove" -> {
                        val whitelist: Whitelist? = Gamemode4Core.whitelists[args[1]]
                        whitelist?.getOwnPlayers()?.forEach {
                            val player: @Nullable Player? = Bukkit.getPlayer(it)
                            if (player != null) {
                                if (player.name.contains(arg)) {
                                    options.add(player.name)
                                }
                            }
                        }
                    }
                    "modify" -> {
                        val properties: MutableSet<String> = mutableSetOf("parent","message")
                        properties.forEach {
                            if (it.contains(arg)) {
                                options.add(it)
                            }
                        }
                    }
                }
            }
            4 -> {
                if (args[0].toLowerCase() == "modify" && args[2].toLowerCase() == "parent") {
                    val arg: String = args[3].toLowerCase()
                    mutableSetOf("add","remove").forEach {
                        if (it.contains(arg)) options.add(it)
                    }
                }
            }
            5 -> {
                if (args[0].toLowerCase() == "modify" && args[2].toLowerCase() == "parent") {
                    val arg: String = args[4]
                    Gamemode4Core.whitelists.forEach { (t, _) ->
                        if (t.contains(arg)) options.add(t)
                    }
                }
            }
        }

        return options
    }
}