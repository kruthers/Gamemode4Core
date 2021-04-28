package com.kruthers.gamemode4core.commands

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.objects.Whitelist
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class WhitelistCommand: CommandExecutor {
    private val invalid: String = "${ChatColor.RED}Invalid argument given, correct usage: /whitelist <add|remove|create|delete|modify|info|set> <whitelist> [...]"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(invalid)
        } else {
            when (args[0].toLowerCase()) {
                "create" -> {
                    if (args.size != 2) {
                        sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /whitelist create <name>")
                    } else {
                        val name: String = args[1]
                        if (Gamemode4Core.whitelists.containsKey(name)) {
                            sender.sendMessage("${ChatColor.RED}Sorry that whitelist already exists")
                        } else {
                            val whitelist: Whitelist = Whitelist(name, mutableSetOf(), mutableSetOf(),"")
                            Gamemode4Core.whitelists[name] = whitelist
                            sender.sendMessage("${ChatColor.GREEN}Sucessfully created whitelisr $name")
                        }
                    }
                }
                "delete" -> {
                    if (args.size != 2) {
                        sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /whitelist delete <whitelist>")
                    } else {
                        val name: String = args[1]
                        if (Gamemode4Core.whitelists.containsKey(name)) {
                            Gamemode4Core.whitelists.remove(name)
                            //TODO Delete whitelist file
                        } else {
                            sender.sendMessage("${ChatColor.RED}Sorry that whitelist does not exists")
                        }
                    }
                }
                "add", "join" -> {
                    if (args.size != 3) {
                        sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /whitelist ${args[0]} <whitelist> <player>")
                    } else {
                        val name: String = args[1]
                        if (Gamemode4Core.whitelists.containsKey(name)) {
                            val whitelist: Whitelist = Gamemode4Core.whitelists[name]!!
                            val player: OfflinePlayer = Bukkit.getOfflinePlayer(name)
                            if (player.uniqueId != null) {
                                whitelist.addPlayer(player.uniqueId)
                                sender.sendMessage("${ChatColor.GREEN}Successful added ${player.name} to the whitelist")
                            } else {
                                sender.sendMessage("${ChatColor.RED}Failed to find user: ${args[2]}")
                            }
                        } else {
                            sender.sendMessage("${ChatColor.RED}Sorry that whitelist does not exists")
                        }
                    }
                }
                "leave", "remove" -> {
                    if (args.size != 3) {
                        sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /whitelist ${args[0]} <whitelist> <player>")
                    } else {
                        val name: String = args[1]
                        if (Gamemode4Core.whitelists.containsKey(name)) {
                            val whitelist: Whitelist = Gamemode4Core.whitelists[name]!!
                            val player: Player? = Bukkit.getPlayer(args[2])
                            if (player != null) {
                                if (whitelist.removePlayer(player.uniqueId)) {
                                    sender.sendMessage("${ChatColor.GREEN}Successful removed ${player.name} to the whitelist")
                                } else {
                                    sender.sendMessage("${ChatColor.RED}That player is no on that whitelist")
                                }
                            } else {
                                sender.sendMessage("${ChatColor.RED}Failed to find user: ${args[2]}")
                            }
                        } else {
                            sender.sendMessage("${ChatColor.RED}Sorry that whitelist does not exists")
                        }
                    }
                }
                "info" -> {
                    if (args.size != 2) {
                        sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /whitelist info <whitelist>")
                    } else {
                        val name: String = args[1]
                        if (Gamemode4Core.whitelists.containsKey(name)) {
                            val whitelist: Whitelist = Gamemode4Core.whitelists[name]!!
                            sender.sendMessage("${ChatColor.AQUA}${whitelist.name}'s Info\n"+
                                    "${ChatColor.GRAY}${ChatColor.BOLD}Rejection message:${ChatColor.RESET} ${whitelist.rejectionMsg}\n"+
                                    "${ChatColor.GRAY}${ChatColor.BOLD}Inherits from: Coming soon"+
                                    "${ChatColor.GRAY}${ChatColor.BOLD}Members from: Comming soon")
                        } else {
                            sender.sendMessage("${ChatColor.RED}Sorry that whitelist does not exists")
                        }
                    }
                }
                "set" -> {
                    if (args.size != 2) {
                        sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /whitelist set <whitelist|none>")
                    } else {
                        val name: String = args[1]
                        when {
                            name == "none" -> {
                                Gamemode4Core.activeWhitelist = ""
                                sender.sendMessage("${ChatColor.GREEN}Disabled whitelist")
                            }
                            Gamemode4Core.whitelists.containsKey(name) -> {
                                Gamemode4Core.activeWhitelist = name
                                sender.sendMessage("${ChatColor.GREEN}Set active whitelist $name")
                            }
                            else -> {
                                sender.sendMessage("${ChatColor.RED}Sorry that whitelist does not exists")
                            }
                        }
                    }
                }
                "modify" -> {
                    if (args.size < 3) {
                        sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /whitelist modify <whitelist> <parent|message>")
                    } else {
                        val name: String = args[1]
                        if (Gamemode4Core.whitelists.containsKey(name)) {
                            val whitelist: Whitelist = Gamemode4Core.whitelists[name]!!
                            when (args[2].toLowerCase()) {
                                "parent" -> {
                                    if (args.size != 5) {
                                        sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /whitelist modify $name parent <add|remove> <whitelist>")
                                    } else {
                                        val name2: String = args[4]
                                        when (args[3]) {
                                            "add" -> {
                                                if (whitelist.addParent(name2)) {
                                                    sender.sendMessage("${ChatColor.GREEN}Successfully added parent $name2")
                                                } else {
                                                    sender.sendMessage("${ChatColor.RED}Failed to add parent $name2")
                                                }
                                            }
                                            "remove" -> {
                                                if (whitelist.removeParent(name2)) {
                                                    sender.sendMessage("${ChatColor.GREEN}Successfully removed parent $name2")
                                                } else {
                                                    sender.sendMessage("${ChatColor.RED}Failed to remove parent $name2")
                                                }
                                            }
                                        }
                                    }
                                }
                                "message" -> {
                                    if (args.size < 4) {
                                        sender.sendMessage("${ChatColor.RED}Invalid argument given, correct usage: /whitelist modify $name message <message...>")
                                    } else {
                                        var message: String = ""
                                        var i:Int = 3
                                        while (i < args.size) {
                                            val word = args[3]
                                            message += "$word "
                                            i++
                                        }
                                        whitelist.rejectionMsg = ChatColor.translateAlternateColorCodes('$',message)
                                    }
                                }
                            }
                        } else {
                            sender.sendMessage("${ChatColor.RED}Sorry that whitelist does not exists")
                        }
                    }
                }
                else -> sender.sendMessage(invalid)
            }
        }

        return true
    }
}