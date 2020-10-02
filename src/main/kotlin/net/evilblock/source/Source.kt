package net.evilblock.source

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.CubedOptions
import net.evilblock.cubed.command.CommandHandler
import net.evilblock.source.server.command.BroadcastCommand
import net.evilblock.pidgin.Pidgin
import net.evilblock.pidgin.PidginOptions
import net.evilblock.source.captcha.CaptchaHandler
import net.evilblock.source.captcha.listener.CaptchaPreventionListeners
import net.evilblock.source.chat.command.AntiSpamToggleCommand
import net.evilblock.source.chat.command.ClearChatCommand
import net.evilblock.source.chat.command.MuteChatCommand
import net.evilblock.source.chat.command.SlowChatCommand
import net.evilblock.source.chat.filter.ChatFilterHandler
import net.evilblock.source.chat.filter.ChatFilterListeners
import net.evilblock.source.chat.filter.command.ChatFilterEditorCommand
import net.evilblock.source.chat.filter.command.ChatFilterImportDefaultsCommand
import net.evilblock.source.chat.spam.ChatSpamListeners
import net.evilblock.source.messaging.command.*
import net.evilblock.source.messaging.listener.ChatIgnoreListeners
import net.evilblock.source.server.prevention.DisallowedCommandsHandler
import net.evilblock.source.server.announcement.AnnouncementHandler
import net.evilblock.source.server.announcement.command.AnnouncementEditorCommand
import net.evilblock.source.server.command.*
import net.evilblock.source.server.listener.ColoredSignListeners
import net.evilblock.source.server.prevention.listener.DisallowedCommandsListeners
import net.evilblock.source.server.listener.HeadNameListeners
import net.evilblock.source.server.listener.TeleportationListeners
import net.evilblock.source.server.prevention.command.DisallowedCommandsEditorCommand
import net.evilblock.source.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

class Source : JavaPlugin() {

    lateinit var pidgin: Pidgin

    override fun onEnable() {
        instance = this

        saveDefaultConfig()

        Cubed.instance.configureOptions(CubedOptions(requireRedis = true))

        pidgin = Pidgin("Source", Cubed.instance.redis.jedisPool!!, PidginOptions(async = true))
        pidgin.registerListener(AnnouncementHandler)
        pidgin.registerListener(ChatFilterHandler)
        pidgin.registerListener(DisallowedCommandsHandler)

        AnnouncementHandler.initialLoad()
        ChatFilterHandler.initialLoad()
        DisallowedCommandsHandler.initialLoad()
        CaptchaHandler.initialLoad()

        CommandHandler.registerClass(ReloadCommand.javaClass)

        CommandHandler.registerClass(AnnouncementEditorCommand.javaClass)
        CommandHandler.registerClass(DisallowedCommandsEditorCommand.javaClass)

        CommandHandler.registerClass(ChatFilterEditorCommand.javaClass)
        CommandHandler.registerClass(ChatFilterImportDefaultsCommand.javaClass)

        CommandHandler.registerClass(AntiSpamToggleCommand.javaClass)
        CommandHandler.registerClass(ClearChatCommand.javaClass)
        CommandHandler.registerClass(MuteChatCommand.javaClass)
        CommandHandler.registerClass(SlowChatCommand.javaClass)

        CommandHandler.registerClass(IgnoreCommand.javaClass)
        CommandHandler.registerClass(IgnoreListClearCommand.javaClass)
        CommandHandler.registerClass(IgnoreListCommand.javaClass)
        CommandHandler.registerClass(IgnoreRemoveCommand.javaClass)
        CommandHandler.registerClass(MessageCommand.javaClass)
        CommandHandler.registerClass(ReplyCommand.javaClass)
        CommandHandler.registerClass(SpyCommand.javaClass)
        CommandHandler.registerClass(ToggleMessagesCommand.javaClass)
        CommandHandler.registerClass(ToggleSoundsCommand.javaClass)

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
        CommandHandler.registerClass(SetSpawnCommand.javaClass)
        CommandHandler.registerClass(SpeedCommand.javaClass)
        CommandHandler.registerClass(SudoCommands.javaClass)
        CommandHandler.registerClass(FeedCommand.javaClass)
        CommandHandler.registerClass(TeleportationCommands.javaClass)
        CommandHandler.registerClass(WorldCommand.javaClass)

        CommandHandler.registerClass(HidePlayerCommand.javaClass)
        CommandHandler.registerClass(ShowPlayerCommand.javaClass)

        server.pluginManager.registerEvents(CaptchaPreventionListeners, this)
        server.pluginManager.registerEvents(ChatSpamListeners, this)
        server.pluginManager.registerEvents(ChatFilterListeners, this)
        server.pluginManager.registerEvents(ChatIgnoreListeners, this)

        server.pluginManager.registerEvents(ColoredSignListeners, this)
        server.pluginManager.registerEvents(DisallowedCommandsListeners, this)
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

    companion object {
        @JvmStatic
        lateinit var instance: Source
    }

}