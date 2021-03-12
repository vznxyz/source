package com.minexd.source

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.CommandHandler
import net.evilblock.pidgin.Pidgin
import net.evilblock.pidgin.PidginOptions
import com.minexd.source.captcha.CaptchaHandler
import com.minexd.source.captcha.listener.CaptchaPreventionListeners
import com.minexd.source.chat.command.AntiSpamToggleCommand
import com.minexd.source.chat.command.ClearChatCommand
import com.minexd.source.chat.command.MuteChatCommand
import com.minexd.source.chat.command.SlowChatCommand
import com.minexd.source.chat.filter.ChatFilterHandler
import com.minexd.source.chat.filter.listener.ChatFilterListeners
import com.minexd.source.chat.filter.command.ChatFilterEditorCommand
import com.minexd.source.chat.filter.command.ChatFilterImportDefaultsCommand
import com.minexd.source.chat.spam.ChatSpamListeners
import com.minexd.source.server.ServerConfig
import com.minexd.source.server.prevention.BlockedCommandsHandler
import com.minexd.source.server.announcement.AnnouncementHandler
import com.minexd.source.server.announcement.command.AnnouncementEditorCommand
import com.minexd.source.server.command.*
import com.minexd.source.server.listener.ColoredSignListeners
import com.minexd.source.server.listener.DisableAchievementsListeners
import com.minexd.source.server.prevention.listener.BlockedCommandsListeners
import com.minexd.source.server.listener.HeadNameListeners
import com.minexd.source.server.listener.TeleportationListeners
import com.minexd.source.server.prevention.command.BlockedCommandsEditorCommand
import com.minexd.source.util.Permissions
import net.evilblock.cubed.serializers.Serializers
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Source : JavaPlugin() {

    companion object {
        @JvmStatic
        lateinit var instance: Source
    }

    lateinit var pidgin: Pidgin
    lateinit var serverConfig: ServerConfig

    override fun onEnable() {
        instance = this

        saveDefaultConfig()

        pidgin = Pidgin("Source", Cubed.instance.redis.jedisPool!!, Serializers.gson, PidginOptions(async = true))
        pidgin.registerListener(AnnouncementHandler)
        pidgin.registerListener(ChatFilterHandler)
        pidgin.registerListener(BlockedCommandsHandler)

        loadServerConfig()

        AnnouncementHandler.initialLoad()
        ChatFilterHandler.initialLoad()
        BlockedCommandsHandler.initialLoad()
        CaptchaHandler.initialLoad()

        CommandHandler.registerClass(ReloadCommand.javaClass)

        CommandHandler.registerClass(AnnouncementEditorCommand.javaClass)
        CommandHandler.registerClass(BlockedCommandsEditorCommand.javaClass)

        CommandHandler.registerClass(ChatFilterEditorCommand.javaClass)
        CommandHandler.registerClass(ChatFilterImportDefaultsCommand.javaClass)

        CommandHandler.registerClass(AntiSpamToggleCommand.javaClass)
        CommandHandler.registerClass(ClearChatCommand.javaClass)
        CommandHandler.registerClass(MuteChatCommand.javaClass)
        CommandHandler.registerClass(SlowChatCommand.javaClass)

        CommandHandler.registerClass(EditLoreCommand.javaClass)
        CommandHandler.registerClass(BroadcastCommand.javaClass)
        CommandHandler.registerClass(ClearCommand.javaClass)
        CommandHandler.registerClass(ColorsCommand.javaClass)
        CommandHandler.registerClass(CraftCommand.javaClass)
        CommandHandler.registerClass(EnchantCommand.javaClass)
        CommandHandler.registerClass(GamemodeCommands.javaClass)
        CommandHandler.registerClass(HeadCommand.javaClass)
        CommandHandler.registerClass(HealCommand.javaClass)
        CommandHandler.registerClass(PingCommand.javaClass)
        CommandHandler.registerClass(RenameCommand.javaClass)
        CommandHandler.registerClass(RepairCommand.javaClass)
        CommandHandler.registerClass(SpeedCommand.javaClass)
        CommandHandler.registerClass(SudoCommands.javaClass)
        CommandHandler.registerClass(FeedCommand.javaClass)
        CommandHandler.registerClass(TeleportationCommands.javaClass)
        CommandHandler.registerClass(WorldCommand.javaClass)

        CommandHandler.registerClass(HidePlayerCommand.javaClass)
        CommandHandler.registerClass(ShowPlayerCommand.javaClass)

        CommandHandler.registerClass(SetSpawnCommand.javaClass)
        if (config.getBoolean("register-spawn-command", true)) {
            CommandHandler.registerClass(SpawnCommand.javaClass)
        }

        server.pluginManager.registerEvents(CaptchaPreventionListeners, this)
        server.pluginManager.registerEvents(ChatSpamListeners, this)
        server.pluginManager.registerEvents(ChatFilterListeners, this)

        server.pluginManager.registerEvents(ColoredSignListeners, this)
        server.pluginManager.registerEvents(BlockedCommandsListeners, this)
        server.pluginManager.registerEvents(DisableAchievementsListeners, this)
        server.pluginManager.registerEvents(HeadNameListeners, this)
        server.pluginManager.registerEvents(TeleportationListeners, this)
    }

    fun systemLog(log: String) {
        for (player in server.onlinePlayers) {
            if (player.isOp || player.hasPermission(Permissions.LOG_VIEW)) {
                player.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}[SOURCE] ${ChatColor.GRAY}$log")
            }
        }
    }

    fun loadServerConfig() {
        val configFile = File(File(dataFolder, "internal"), "server-config.json")
        if (configFile.exists()) {
            Files.newReader(configFile, Charsets.UTF_8).use { reader ->
                serverConfig = Serializers.gson.fromJson(reader.readLine(), object : TypeToken<ServerConfig>() {}.type)
            }
        } else {
            serverConfig = ServerConfig()
        }
    }

    fun saveServerConfig() {
        val configFile = File(File(dataFolder, "internal"), "server-config.json")
        configFile.parentFile.mkdirs()

        Files.write(Serializers.gson.toJson(serverConfig), configFile, Charsets.UTF_8)
    }
    
    fun getSpawnLocation(): Location {
        return serverConfig.spawnLocation ?: server.worlds[0].spawnLocation
    }

}