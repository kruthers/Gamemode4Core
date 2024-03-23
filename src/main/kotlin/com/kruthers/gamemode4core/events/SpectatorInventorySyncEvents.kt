package com.kruthers.gamemode4core.events

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent
import com.kruthers.gamemode4core.Gamemode4Core
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class SpectatorInventorySyncEvents : Listener {

    companion object {
        private val spectateInventories: HashMap<Player, Map<Int, ItemStack>> = hashMapOf()
        private val targets: HashMap<Player, Player> = hashMapOf()

        fun restoreInventory(p: Player) {
            val inv = spectateInventories[p] ?: return
            inv.forEach { (index, item) ->
                p.inventory.setItem(index, item)
            }
            spectateInventories.remove(p)
        }

        fun saveInventory(p: Player) {
            if (spectateInventories.containsKey(p)) return
            val inv = p.inventory.mapIndexed { index, item -> index to item }.toMap()
            spectateInventories[p] = inv
        }
    }

    private fun copyInv(spectator: Player, inv: Inventory) {
        spectator.inventory.clear()
        inv.forEachIndexed { index, item ->
            spectator.inventory.setItem(index, item)
        }
    }

    @EventHandler
    fun onSpectateStart(event: PlayerStartSpectatingEntityEvent){
        val target = event.newSpectatorTarget
        if (target is Player) {
            val player = event.player
            targets[player] = target
            saveInventory(player)
            this.copyInv(player, target.inventory)
        } else {
            //restore their inventory
            restoreInventory(event.player)
        }
    }

    @EventHandler
    fun onSpectateEnd(event: PlayerStopSpectatingEntityEvent) {
        targets.remove(event.player)
        restoreInventory(event.player)
    }

    @EventHandler
    fun onInvUpdate(event: InventoryClickEvent) {
        val target = event.view.player
        if (targets.values.contains(target)) {
            Bukkit.getScheduler().runTask(Gamemode4Core.instance, Runnable {
                targets.filter { it.value == target }.forEach { (player, _) ->
                    this.copyInv(player, target.inventory)
                }
            })
        }
    }
}