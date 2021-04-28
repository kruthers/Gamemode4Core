package com.kruthers.gamemode4core

import com.kruthers.gamemode4core.commands.*
import com.kruthers.gamemode4core.commands.tabcompleaters.*
import com.kruthers.gamemode4core.events.DimensionEvents
import com.kruthers.gamemode4core.events.PlayerConnectionEvents
import com.kruthers.gamemode4core.events.PlayerFrozenEvents
import com.kruthers.gamemode4core.events.StatusEvent
import com.kruthers.gamemode4core.objects.Whitelist
import com.kruthers.gamemode4core.utils.initStorageFolders
import com.kruthers.gamemode4core.utils.loadWhitelists
import net.md_5.bungee.api.ChatColor
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.command.CommandExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import org.dynmap.DynmapAPI
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Gamemode4Core(): JavaPlugin() {

    companion object {
        var playersFrozen: Boolean = false
        val whitelists: HashMap<String, Whitelist> = HashMap()
        val watchingPlayers: HashMap<Player,UUID> = HashMap();
        val backLocations: HashMap<Player,MutableList<Location>> = HashMap()

        var activeWhitelist: String = ""

        lateinit var buildBossBar: BossBar

        lateinit var permission: Permission
        lateinit var dynmapAPI: DynmapAPI

        var dynmap: Boolean = false
        var placeholder: Boolean = false;
    }

    override fun onEnable() {
        this.logger.info("Enabling Gamemode 4 Core by kruthers")

        this.logger.info("Loading Required Dependencies")
        val pluginManager: PluginManager = this.getServer().getPluginManager();
        this.logger.info("Loading ProtocolLib")
        if (pluginManager.getPlugin("ProtocolLib") == null) {
            this.logger.severe("Failed to load required dependency, ProtocolLib")
            server.pluginManager.disablePlugin(this)
            return
        }
        this.logger.info("Loading Vault")
        if (!setupVaultPermissions()) {
            this.logger.severe("Failed to load required dependency, Vault")
            server.pluginManager.disablePlugin(this)
            return
        }

        this.logger.info("Loading optional depedencies")
        if (pluginManager.getPlugin("Dynmap") != null) {
            this.logger.info("Found dynmap, hooking into it.")
            dynmapAPI = pluginManager.getPlugin("Dynmap") as DynmapAPI
            dynmap = true
        }
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            this.logger.info("Loading PlaceholderAPI hooks")
            placeholder = true
        }

        this.logger.info("Loading config...")
        this.config.options().copyDefaults(true)
        this.saveConfig()

        this.logger.info("Config successfully loaded, loading in all data files...")
        if (!initStorageFolders(this)) {
            this.logger.warning("Failed to setup storage folders for plugins, disabling plugin")
            server.pluginManager.disablePlugin(this)
            return
        }


        loadWhitelists(this)

        this.logger.info("Loaded data files, loading in scoreboard & bossbars...")
        buildBossBar = Bukkit.getServer().createBossBar(NamespacedKey.fromString("buildmode",this)!!,"${ChatColor.GOLD}Build Mode is engaged",BarColor.RED,BarStyle.SOLID)
        buildBossBar.isVisible = true;
        buildBossBar.progress = 1.0

        this.logger.info("Loaded scoreboard and bossbar data, registering commands...")
        val comingSoon: CommandExecutor = ComingSoon();
        this.server.getPluginCommand("gamemode4core")?.setExecutor( CoreCommand(this) )
        this.server.getPluginCommand("whitelist")?.setExecutor( comingSoon ) //TODO
        this.server.getPluginCommand("streammode")?.setExecutor( StreamerModeCommand(this) )
        this.server.getPluginCommand("buildmode")?.setExecutor( BuildModeCommand(this) )
        this.server.getPluginCommand("watch")?.setExecutor( WatchCommand(this) )
        this.server.getPluginCommand("watchconfirm")?.setExecutor( WatchConfirmCommand(this) )
        this.server.getPluginCommand("unwatch")?.setExecutor( UnWatchCommand(this) )
        this.server.getPluginCommand("tpa")?.setExecutor( TpaCommand(this) )
        this.server.getPluginCommand("back")?.setExecutor( BackCommand(this) )
        this.server.getPluginCommand("freeze")?.setExecutor( FreezeCommand(this) )
        this.server.getPluginCommand("unfreeze")?.setExecutor( UnFreezeCommand(this) )

        this.logger.info("All Commands registered, loading tab completer's")
        val nullTabCompleter: NullTabCompleter = NullTabCompleter()
        this.server.getPluginCommand("gamemode4core")?.tabCompleter = CoreCommandTabCompleter()
//        this.server.getPluginCommand("whitelist")?.tabCompleter = WhitelistTabCompleter()
        this.server.getPluginCommand("streammode")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("buildmode")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("watch")?.tabCompleter = WatchTabCompleter()
        this.server.getPluginCommand("watchconfirm")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("unwatch")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("tpa")?.tabCompleter = TpaTabCompleter()
        this.server.getPluginCommand("back")?.tabCompleter = nullTabCompleter
//        this.server.getPluginCommand("forward")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("freeze")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("unfreeze")?.tabCompleter = nullTabCompleter

        this.logger.info("Loaded all tab completer's, registering events...")
        this.server.pluginManager.registerEvents(PlayerConnectionEvents(this), this)
        this.server.pluginManager.registerEvents(PlayerFrozenEvents(this), this)
        this.server.pluginManager.registerEvents(DimensionEvents(this), this)
        this.server.pluginManager.registerEvents(StatusEvent(this), this)

        this.logger.info("Loaded events")
        this.server.consoleSender.sendMessage("${ChatColor.GREEN}Gamemode 4 Core is now loaded in and ready to go")

    }


    override fun onDisable() {
        this.logger.info("Disabling Gamemode 4 core")
        Bukkit.getServer().removeBossBar(NamespacedKey.fromString("buildmode",this)!!)


        this.server.consoleSender.sendMessage("${ChatColor.RED}Gamemode 4 Core is now disabled")
    }

    private fun setupVaultPermissions(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp: RegisteredServiceProvider<Permission> = server.servicesManager.getRegistration(Permission::class.java)
                ?: return false
        permission = rsp.provider
        return true
    }
}