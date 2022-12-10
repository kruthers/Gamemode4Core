package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import net.kyori.adventure.text.Component
import net.luckperms.api.LuckPerms
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

class PermissionEvent(val plugin: Gamemode4Core, perms: LuckPerms) {

    init {
        val eventBus = perms.eventBus

        eventBus.subscribe(NodeAddEvent::class.java, this::onNodeAddEvent)
    }

    private fun onNodeAddEvent(event: NodeAddEvent) {
        val target = event.target
        if (event.isUser && target is User) {
            val node = event.node
            if (node.type == NodeType.INHERITANCE && node is InheritanceNode) {
                if (this.plugin.config.getStringList("donor_tagging.groups").contains(node.groupName)) {
                    val uuid = target.uniqueId
                    val player = Bukkit.getOfflinePlayer(uuid)
                    if (player.isOnline && player.player != null) {
                        player.player!!.scoreboardTags.add(this.plugin.config.getString("donor_tagging.tag")?: "donor")
                    }
                }
            }
        }
    }

    private fun onNodeRemoveEvent(event: NodeRemoveEvent) {
        val target = event.target
        if (event.isUser && target is User) {
            val node = event.node
            if (node.type == NodeType.INHERITANCE && node is InheritanceNode) {
                val groups = this.plugin.config.getStringList("donor_tagging.groups")
                if (groups.contains(node.groupName)) {
                    val uuid = target.uniqueId
                    val player = Bukkit.getOfflinePlayer(uuid)
                    if (player.isOnline && player.player != null) {
                        var stillDonor = false
                        //check if they still have any other donor roles
                        target.getInheritedGroups(target.queryOptions).forEach { group ->
                            if (!stillDonor && groups.contains(group.name)) stillDonor = true
                        }

                        //if they have no donor roles, remove the donor tag
                        if (!stillDonor) player.player!!.scoreboardTags.remove(this.plugin.config.getString("donor_tagging.tag")?: "donor")
                    }
                }
            }
        }
    }


}