package net.evilblock.source

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.CubedOptions
import net.evilblock.cubed.command.CommandHandler
import net.evilblock.cubed.command.def.BroadcastCommand
import net.evilblock.source.captcha.CaptchaHandler
import net.evilblock.source.captcha.listener.CaptchaPreventionListeners
import net.evilblock.source.chat.command.AntiSpamToggleCommand
import net.evilblock.source.chat.command.ClearChatCommand
import net.evilblock.source.chat.command.MuteChatCommand
import net.evilblock.source.chat.command.SlowChatCommand
import net.evilblock.source.chat.filter.ChatFilterListeners
import net.evilblock.source.chat.spam.ChatSpamListeners
import net.evilblock.source.messaging.command.*
import net.evilblock.source.messaging.listener.ChatIgnoreListeners
import net.evilblock.source.server.command.*
import net.evilblock.source.server.listener.ColoredSignListeners
import net.evilblock.source.server.listener.DisallowedCommandsListeners
import net.evilblock.source.server.listener.HeadNameListeners
import net.evilblock.source.server.listener.TeleportationListeners
import org.bukkit.plugin.java.JavaPlugin

class Source : JavaPlugin() {

    override fun onEnable() {
        instance = this

        saveDefaultConfig()

        Cubed.instance.configureOptions(CubedOptions(requireRedis = true))

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

        CommandHandler.registerClass(BroadcastCommand.javaClass)
        CommandHandler.registerClass(ClearCommand.javaClass)
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

        CaptchaHandler.initialLoad()
    }

    companion object {
        @JvmStatic
        lateinit var instance: Source
    }

}