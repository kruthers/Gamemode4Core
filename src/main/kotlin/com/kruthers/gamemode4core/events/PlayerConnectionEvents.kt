package com.kruthers.gamemode4core.events

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.modes.BuildMode
import com.kruthers.gamemode4core.modes.StreamerMode
import com.kruthers.gamemode4core.modes.Watcher
import com.kruthers.gamemode4core.utils.getMessage
import com.kruthers.gamemode4core.utils.loadPlayerData
import com.kruthers.gamemode4core.utils.playerHasData
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Score
import java.util.*

class PlayerConnectionEvents(val pl: Gamemode4Core): Listener {
    private val tagPrefix: String = "\u1747gm4Core_"

    //regex
    private val usernameRegex: Regex = Regex("${tagPrefix}username_([\\w_]{3,16})")

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player: Player = event.player;

        //Gives them a tag containing there current username to monitor for changes
        if (!player.hasPlayedBefore()) {
            // player has not played before so just gives them the tag it will also handel first join events
            val location: Location = getPlayerSpawn();
            player.setBedSpawnLocation(location,true)
            player.teleport(location)

            player.addScoreboardTag("${tagPrefix}username_${player.name}")

        } else {
            //player has played before to searches there tags to try and find the saved username
            var username: String = ""
            player.scoreboardTags.forEach {
                val found = usernameRegex.find(it)
                if (found!=null) {
                    username = found.groups[1]?.value.toString()
                }
            }

            // checks if username is saved
            if (username == "") {
                // -> username is not saved, so will just add the tag
                player.addScoreboardTag("${tagPrefix}username_${player.name}")
            } else {
                // -> username is found, will check if it maches there current username
                if (username != player.name) {
                    // -> if it does not match it will remove it and add a new tag with the update name
                    player.addScoreboardTag("${tagPrefix}username_${player.name}")
                    player.removeScoreboardTag("${tagPrefix}username_${username}")

                    //TODO check if works
                    //Now will parse all the scoreboards that are tracked and copy the scores over
                    val scoreboards: MutableList<String> = pl.config.getStringList("scoreboard.tracked")
                    scoreboards.forEach {
                        val scoreboard: Objective? = Bukkit.getServer().scoreboardManager?.mainScoreboard?.getObjective(it)
                        val score: Score? = scoreboard?.getScore(username)
                        score?.score.also { scoreboard?.getScore(player.name)?.score = it as Int }
                    }
                }
            }
        }


        // This handels players using watch, build and stream mode.
        //first checks if they have any data
        if (playerHasData(pl, player)) {
            //if they have saved data they will load it and check for diffrent modes
            val playerData: YamlConfiguration = loadPlayerData(pl, player)

            //if stream mode is enabled it will send a reminder as long as they still have perms
            if (playerData.getBoolean("mode.streamer")) {
                if (player.hasPermission("gm4core.mode.streamer")) {
                    player.sendMessage(getMessage(pl, "streammode.join"))
                } else {
                    StreamerMode.toggle(pl,player)
                }
            }

            //the other modes can only have 1 enabled so it will just check which one (if any) is enabled and send reminders
            when (playerData.getString("mode.current")) {
                "build" -> {
                    if (player.hasPermission("gm4core.mode.build")) {
                        player.sendMessage(getMessage(pl, "buildmode.join", player))
                        Gamemode4Core.buildBossBar.addPlayer(player)
                    } else {
                        BuildMode.disable(pl, player, playerData, true);
                    }
                }
                "watch" -> {
                    if (player.hasPermission("gm4core.mode.watch")) {
                        // get the uuid of the player being watched and check if they are online or not (sends different messages)
                        val targetUUID: String? = playerData.getString("storage.watch.target")
                        if (targetUUID != null) {
                            val target: OfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(targetUUID))
                            if (target.isOnline) {
                                player.sendMessage(getMessage(pl, "watch.join_online").replace("{target}", target.name!!))
                            } else {
                                player.sendMessage(getMessage(pl, "watch.join_offline").replace("{target}", target.name!!))
                            }
                            Gamemode4Core.watchingPlayers[player] = UUID.fromString(targetUUID)
                        } else {
                            Watcher.disable(pl, player, playerData, true)
                        }
                    } else {
                        Watcher.disable(pl, player, playerData, true);
                    }
                }
            }
        }

        // If the server is in freeze mode it will send a message informing them
        if (Gamemode4Core.playersFrozen) {
            event.player.sendMessage("${ChatColor.AQUA}Everyone is currently frozen while the mods resolve an issue, please stand still")
            event.player.sendTitle("${ChatColor.AQUA}Everyone is currently frozen while the mods resolve an issue, please stand still","",0,200,20)
        }

    }


    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player: Player = event.player

        //checks if the player is being watched
        if (Gamemode4Core.watchingPlayers.values.contains(player.uniqueId)) {
            // if they are it will send a message to the watcher informing them
            Gamemode4Core.watchingPlayers.forEach { watcher, target ->
                if (target == player.uniqueId) {
                    watcher.sendMessage(getMessage(pl,"watch.logout",watcher).replace("{target}",player.name))
                }
            }
        }

        // if they are watching someone it will remove them from the monitoring list
        if (Gamemode4Core.watchingPlayers.keys.contains(player)) {
            Gamemode4Core.watchingPlayers.remove(player)
        }
    }


    private fun getPlayerSpawn(): Location {
        val config: FileConfiguration = pl.config

        val x: Double = config.getDouble("spawn.player.x")
        val y: Double = config.getDouble("spawn.player.y")
        val z: Double = config.getDouble("spawn.player.z")

        val pitch: Float = config.getDouble("spawn.player.pitch").toFloat()
        val yaw: Float = config.getDouble("spawn.player.yaw").toFloat()

        val worldName: String = config.getString("spawn.player.world").toString()
        var world: World? = Bukkit.getWorld(worldName)
        if (world == null) {
            world = Bukkit.getWorlds()[0]
        }

        return Location(world,x,y,z,yaw,pitch)
    }

}