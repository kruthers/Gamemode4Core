package com.kruthers.gamemode4core.utils

import com.kruthers.gamemode4core.Gamemode4Core
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun playerAddGroup(player: Player, group: String?): Boolean {
    if (Gamemode4Core.permission.groups.contains(group) && group != null) {
        Bukkit.getServer().worlds.forEach {
            if (it != null) {
                Gamemode4Core.permission.playerAddGroup(it.name, Bukkit.getOfflinePlayer(player.uniqueId),group)
            }
        }

    } else {
        return false
    }

    return true
}

fun playerRemoveGroup(player: Player, group: String?): Boolean {
    if (Gamemode4Core.permission.groups.contains(group) && group != null) {
        Bukkit.getServer().worlds.forEach {
            if (it != null) {
                Gamemode4Core.permission.playerRemoveGroup(it.name, Bukkit.getOfflinePlayer(player.uniqueId),group)
            }
        }

    } else {
        return false
    }

    return true
}