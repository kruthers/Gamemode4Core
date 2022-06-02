package com.kruthers.gamemode4core

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.meta.SimpleCommandMeta
import cloud.commandframework.minecraft.extras.AudienceProvider
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler
import cloud.commandframework.paper.PaperCommandManager
import com.kruthers.gamemode4core.commands.*
import com.kruthers.gamemode4core.events.*
import com.kruthers.gamemode4core.utils.configVersionCheck
import com.kruthers.gamemode4core.utils.initStorageFolders
import com.kruthers.gamemode4core.utils.loadWarps
import net.kyori.adventure.text.Component
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.Function


class Gamemode4Core : JavaPlugin() {

    companion object {
        var playersFrozen: Boolean = false
        val watchingPlayers: HashMap<Player,UUID> = HashMap()
        val backLocations: HashMap<UUID,MutableList<Location>> = HashMap()

        lateinit var modModeBossBar: BossBar

        lateinit var luckPermsAPI: LuckPerms

        var placeholder: Boolean = false
    }

    override fun onEnable() {
        this.logger.info("Enabling Gamemode 4 Core by kruthers")
        this.logger.info("Loading Required Dependencies")
        val pluginManager: PluginManager = this.server.pluginManager
        this.logger.info("Loading Luck perms")
        if (!setupLuckPerms()) {
            this.logger.severe("Failed to load required dependency, Luck Perms")
            server.pluginManager.disablePlugin(this)
            return
        }

        this.logger.info("Loading optional dependencies")
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            this.logger.info("Loading PlaceholderAPI hooks")
            placeholder = true
        }


        this.logger.info("Loading config...")
        this.config.options().copyDefaults(true)
        //check if storage files need updating
        configVersionCheck(this,config)

        this.saveConfig()
        loadWarps(this)

        this.logger.info("Config successfully loaded, loading in all data files...")
        if (!initStorageFolders(this)) {
            this.logger.warning("Failed to setup storage folders for plugins, disabling plugin")
            server.pluginManager.disablePlugin(this)
            return
        }

        this.logger.info("Loaded data files, loading in scoreboard & bossbars...")
        modModeBossBar = Bukkit.getServer().createBossBar(NamespacedKey.fromString("mode_mode",this)!!,
            "${ChatColor.GREEN}${ChatColor.BOLD}Mode Mode Enabled",BarColor.YELLOW,BarStyle.SOLID)
        modModeBossBar.isVisible = true
        modModeBossBar.progress = 1.0

        this.logger.info("Loaded scoreboard and bossbar data, loading commands...")
        val cmdManager: PaperCommandManager<CommandSender> = PaperCommandManager<CommandSender>(
            this,
            CommandExecutionCoordinator.simpleCoordinator(),
            Function.identity(),
            Function.identity()
        )

        try {
            cmdManager.registerBrigadier()
        } catch (e: Exception) {
            this.logger.warning("Failed to initialize Brigadier support: " + e.message)
        }

        MinecraftExceptionHandler<CommandSender>()
            .withDefaultHandlers()
            .withDecorator { component ->
                Component.text()
                    .append(component)
                    .build()
            }
            .apply(cmdManager, AudienceProvider.nativeAudience())

        val annotationParser: AnnotationParser<CommandSender> = AnnotationParser<CommandSender>(
            cmdManager,
            CommandSender::class.java
        ) {
            SimpleCommandMeta.empty()
        }

        annotationParser.parse(CoreCommand(this))
        annotationParser.parse(ModeCommands(this))
        annotationParser.parse(WatchCommands(this))
        annotationParser.parse(FreezeCommand(this))
        annotationParser.parse(TpaCommand(this))
        annotationParser.parse(BackCommand(this))
        annotationParser.parse(WarpCommand(this))
        annotationParser.parse(ManageWarpsCommand(this))

        this.logger.info("Loaded all tab completers, registering events...")
        this.server.pluginManager.registerEvents(PlayerConnectionEvents(this), this)
        this.server.pluginManager.registerEvents(PlayerFrozenEvents(this), this)
        this.server.pluginManager.registerEvents(DimensionEvents(this), this)
        this.server.pluginManager.registerEvents(StatusEvent(this), this)
        this.server.pluginManager.registerEvents(PlayerRespawnEvent(this),this)
        this.server.pluginManager.registerEvents(TimeEvents(this),this)
        this.server.pluginManager.registerEvents(GriefEvents(this),this)

        this.logger.info("Loaded events")
        this.server.consoleSender.sendMessage("${ChatColor.GREEN}Gamemode 4 Core is now loaded in and ready to go")

    }


    override fun onDisable() {
        this.logger.info("Disabling Gamemode 4 core")
        Bukkit.getServer().removeBossBar(NamespacedKey.fromString("mode_mode",this)!!)


        this.server.consoleSender.sendMessage("${ChatColor.RED}Gamemode 4 Core is now disabled")
    }

    private fun setupLuckPerms(): Boolean {
        val lpProvider = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
        if (lpProvider != null) {
            this.logger.info("Loading LuckPerms hooks")
            luckPermsAPI = lpProvider.provider

            return true
        }
        return false
    }

    fun addTPALocation(player: Player) {
        var locations: MutableList<Location> = backLocations[player.uniqueId] ?: mutableListOf()

        locations.add(0,player.location)

        if (locations.size > this.config.getInt("stored_locations.back")) {
            locations = locations.subList(0,this.config.getInt("stored_locations.back"))
        }

        backLocations[player.uniqueId] = locations

    }

}