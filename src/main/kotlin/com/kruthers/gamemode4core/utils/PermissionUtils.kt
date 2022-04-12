package com.kruthers.gamemode4core.utils

import com.kruthers.gamemode4core.Gamemode4Core
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.entity.Player

fun playerAddGroup(player: Player, group: String?): Boolean {
    return lpPlayerAddGroup(player, group)
}

fun playerRemoveGroup(player: Player, group: String?): Boolean {
    return lpPlayerRemoveGroup(player, group)
}

private fun lpPlayerAddGroup(player: Player, groupName: String?): Boolean {
    if (groupName == null) return false

    val user = Gamemode4Core.luckPermsAPI.userManager.loadUser(player.uniqueId).get()
    val group = Gamemode4Core.luckPermsAPI.groupManager.getGroup(groupName) ?: return false

    user.data().add(InheritanceNode.builder(group).build())
    Gamemode4Core.luckPermsAPI.userManager.saveUser(user)

    return true
}

private fun lpPlayerRemoveGroup(player: Player, groupName: String?): Boolean {
    if (groupName == null) return false

    val user = Gamemode4Core.luckPermsAPI.userManager.loadUser(player.uniqueId).get()
    val group = Gamemode4Core.luckPermsAPI.groupManager.getGroup(groupName) ?: return false

    if (user.getInheritedGroups(user.queryOptions).contains(group)) {
        user.data().remove(InheritanceNode.builder(group).build())
    }

    Gamemode4Core.luckPermsAPI.userManager.saveUser(user)

    return true
}
