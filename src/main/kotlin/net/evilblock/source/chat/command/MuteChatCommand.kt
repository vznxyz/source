package net.evilblock.source.chat.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.chat.ChatSettings
import net.evilblock.source.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object MuteChatCommand {

    @Command(
        names = ["mutechat", "mute-chat", "source mute-chat"],
        description = "Mutes the chat",
        permission = Permissions.MUTE_CHAT,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        ChatSettings.muted = !ChatSettings.muted
        Bukkit.broadcastMessage("${ChatColor.LIGHT_PURPLE}Public chat has been ${if (ChatSettings.muted) "" else "un"}muted by ${sender.name}.")
    }

}