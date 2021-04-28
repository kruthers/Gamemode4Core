package com.kruthers.gamemode4core.objects

import com.kruthers.gamemode4core.Gamemode4Core
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import java.util.*

class Whitelist(val name: String, val players: MutableSet<UUID>, val inherits: MutableSet<String>, var rejectionMsg: String) {

    init {
        if (rejectionMsg == "") {
            rejectionMsg = "${ChatColor.RED}Sorry you are not whitelist on this server"
        }
    }

    fun saveWhitelist() {
        //TODO whitelist saving
    }

    fun addPlayer(player: UUID) {
        players.add(player)
        this.saveWhitelist()
    }

    fun removePlayer(player: UUID): Boolean {
        return if (players.contains(player)) {
            players.remove(player)
            true
        } else {
            false
        }
    }

    fun addParent(name: String): Boolean {
        return if (Gamemode4Core.whitelists.containsKey(name)) {
            inherits.add(name)
            true
        } else {
            false
        }
    }

    fun removeParent(name: String): Boolean {
        return if (inherits.contains(name)) {
            inherits.remove(name)
            true
        } else {
            false
        }
    }

    fun getOwnPlayers(): MutableSet<UUID> {
        return players
    }

    fun getAllPlayers(): MutableSet<UUID> {
        val players: MutableSet<UUID> = this.players
        inherits.forEach {
            if (Gamemode4Core.whitelists.containsKey(it)) {
                players.addAll(Gamemode4Core.whitelists.get(it)?.getAllPlayers()!!)
            } else {
                inherits.remove(it)
            }
        }

        return players

    }

    fun checkPlayer(player: Player): Boolean {
        return getAllPlayers().contains(player.uniqueId)
    }


}