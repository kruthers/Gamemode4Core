package com.kruthers.gamemode4core

import com.kruthers.gamemode4core.commands.*
import com.kruthers.gamemode4core.commands.tabcompleaters.*
import com.kruthers.gamemode4core.events.*
import com.kruthers.gamemode4core.utils.configVersionCheck
import com.kruthers.gamemode4core.utils.initStorageFolders
import com.kruthers.gamemode4core.utils.loadWarps
import net.luckperms.api.LuckPerms
import net.md_5.bungee.api.ChatColor
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class Gamemode4Core : JavaPlugin() {

    companion object {
        var playersFrozen: Boolean = false
        val watchingPlayers: HashMap<Player,UUID> = HashMap();
        val backLocations: HashMap<UUID,MutableList<Location>> = HashMap()

        lateinit var modModeBossBar: BossBar

        lateinit var permission: Permission
        lateinit var luckPermsAPI: LuckPerms

        var placeholder: Boolean = false;
        var luckperms: Boolean = false;
    }

    override fun onEnable() {
        this.logger.info("Enabling Gamemode 4 Core by kruthers")
        this.logger.info("Loading Required Dependencies")
        val pluginManager: PluginManager = this.getServer().getPluginManager();
        this.logger.info("Loading Vault")
        if (!setupVaultPermissions()) {
            this.logger.severe("Failed to load required dependency, Vault")
            server.pluginManager.disablePlugin(this)
            return
        }

        this.logger.info("Loading optional depedencies")
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            this.logger.info("Loading PlaceholderAPI hooks")
            placeholder = true
        }
        val lpProvider = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
        if (lpProvider != null) {
            this.logger.info("Loading LuckPerms hooks")
            luckPermsAPI = lpProvider.provider
            luckperms = true
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
        modModeBossBar = Bukkit.getServer().createBossBar(NamespacedKey.fromString("mode_mode",this)!!,"${ChatColor.GREEN}${ChatColor.BOLD}Mod Mode is engaged",BarColor.YELLOW,BarStyle.SOLID)
        modModeBossBar.isVisible = true;
        modModeBossBar.progress = 1.0

        this.logger.info("Loaded scoreboard and bossbar data, registering commands...")
//        val comingSoon = ComingSoon();
        this.server.getPluginCommand("gamemode4core")?.setExecutor( CoreCommand(this) )
        this.server.getPluginCommand("streammode")?.setExecutor( StreamerModeCommand(this) )
        this.server.getPluginCommand("modmode")?.setExecutor( ModModeCommand(this) )
        this.server.getPluginCommand("watch")?.setExecutor( WatchCommand(this) )
        this.server.getPluginCommand("unwatch")?.setExecutor( UnwatchCommand(this) )
        this.server.getPluginCommand("watchconfirm")?.setExecutor( WatchConfirmCommand(this) )
        this.server.getPluginCommand("tpa")?.setExecutor( TpaCommand(this) )
        this.server.getPluginCommand("back")?.setExecutor( BackCommand(this) )
        this.server.getPluginCommand("freeze")?.setExecutor( FreezeCommand(this) )
        this.server.getPluginCommand("unfreeze")?.setExecutor( UnFreezeCommand(this) )
        this.server.getPluginCommand("warp")?.setExecutor(WarpCommand(this))
        this.server.getPluginCommand("managewarp")?.setExecutor(ManageWarpsCommand(this))

        this.logger.info("All Commands registered, loading tab completer's")
        val nullTabCompleter = NullTabCompleter()
        this.server.getPluginCommand("gamemode4core")?.tabCompleter = CoreCommandTabCompleter()
        this.server.getPluginCommand("streammode")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("modmode")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("watch")?.tabCompleter = WatchTabCompleter()
        this.server.getPluginCommand("unwatch")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("watchconfirm")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("tpa")?.tabCompleter = TpaTabCompleter()
        this.server.getPluginCommand("back")?.tabCompleter = nullTabCompleter
//        this.server.getPluginCommand("forward")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("freeze")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("unfreeze")?.tabCompleter = nullTabCompleter
        this.server.getPluginCommand("warp")?.tabCompleter = WarpTabCompleter()
        this.server.getPluginCommand("managewarp")?.tabCompleter = ManageWarpTabCompleter()

        this.logger.info("Loaded all tab completer's, registering events...")
        this.server.pluginManager.registerEvents(PlayerConnectionEvents(this), this)
        this.server.pluginManager.registerEvents(PlayerFrozenEvents(this), this)
        this.server.pluginManager.registerEvents(DimensionEvents(this), this)
        this.server.pluginManager.registerEvents(StatusEvent(this), this)
        this.server.pluginManager.registerEvents(PlayerRespawnEvent(this),this)
        this.server.pluginManager.registerEvents(TimeEvents(this),this)

        this.logger.info("Loaded events")
        this.server.consoleSender.sendMessage("${ChatColor.GREEN}Gamemode 4 Core is now loaded in and ready to go")

    }


    override fun onDisable() {
        this.logger.info("Disabling Gamemode 4 core")
        Bukkit.getServer().removeBossBar(NamespacedKey.fromString("mode_mode",this)!!)


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

    fun addTPALocation(player: Player) {
        var locations: MutableList<Location> = backLocations[player.uniqueId] ?: mutableListOf()

        locations.add(0,player.location)

        if (locations.size > this.config.getInt("stored_locations.back")) {
            locations = locations.subList(0,this.config.getInt("stored_locations.back"))
        }

        backLocations[player.uniqueId] = locations

    }

}