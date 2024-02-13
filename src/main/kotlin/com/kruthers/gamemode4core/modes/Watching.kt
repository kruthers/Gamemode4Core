package com.kruthers.gamemode4core.modes

import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import com.kruthers.gamemode4core.utils.getPlayerDataFile
import com.kruthers.gamemode4core.utils.loadPlayerData
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.GameMode
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class Watching {

    companion object {

        fun enable (plugin: Gamemode4Core, player: Player, target: Player): YamlConfiguration {
            var playerData: YamlConfiguration = loadPlayerData(plugin, player)

            //enter watch mode
            if (!playerData.getBoolean("mode.mod_mode")) {
                playerData = ModMode.enable(plugin, player, playerData)
                playerData.set("storage.watching.watchEntrance",true)
            } else {
                playerData.set("storage.watching.watchEntrance",false)
            }

            //check if they are already watching someone
            //if they are not then will save more data
            if (!playerData.getBoolean("mode.watching")) {
                //save data
                playerData.set("mode.watching",true)
                playerData.set("storage.watching.location",player.location)
                playerData.set("storage.watching.gamemode",player.gameMode.toString())
            }

            //save target data
            playerData.set("storage.watching.target",target.uniqueId.toString())

            //teleport to target and set gamemode
            player.gameMode = GameMode.SPECTATOR

            // check if spectating inside someone
            player.spectatorTarget?.let { player.spectatorTarget = null }

            player.teleport(target.location)

            //message
            player.sendMessage(getMessage(plugin,"watch.start",player,
                TagResolver.resolver(Placeholder.unparsed("target",target.name))))

            //save data
            playerData.save(getPlayerDataFile(plugin, player))

            return playerData

        }

        fun disable(plugin: Gamemode4Core, player: Player, pd: YamlConfiguration, disableModMode: Boolean): YamlConfiguration {
            var playerData: YamlConfiguration = pd;
            //set watching to false
            playerData.set("mode.watching",false);

            //set no target
            playerData.set("storage.watching.target",null);

            //load old data
            playerData.getLocation("storage.watching.location")?.let { player.teleport(it) }
            playerData.getString("storage.watching.gamemode")?.let { player.gameMode = GameMode.valueOf(it) }

            //message
            player.sendMessage(getMessage(plugin,"watch.stop",player))

            //save data
            playerData.save(getPlayerDataFile(plugin, player))

            Gamemode4Core.watchingPlayers.remove(player)

            //check if they only entered mod mode to use watch mode
            if (disableModMode && playerData.getBoolean("storage.watching.watchEntrance")) {
                playerData = ModMode.disable(plugin,player,playerData)
            }

            return playerData
        }

    }

}