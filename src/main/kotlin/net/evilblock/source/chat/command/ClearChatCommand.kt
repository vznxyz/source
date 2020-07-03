package net.evilblock.source.chat.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.source.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ClearChatCommand {

    @Command(
        names = ["clearchat", "cc"],
        description = "Clears the chat",
        permission = Permissions.CLEAR_CHAT,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        Tasks.async {
            Bukkit.getOnlinePlayers().forEach {
                if (!it.hasPermission(Permissions.CLEAR_CHAT_BYPASS)) {
                    for (i in 1..100) it.sendMessage("")
                }

                if (sender is Player) {
                    it.sendMessage("${ChatColor.LIGHT_PURPLE}The chat has been cleared by " + sender.name + ".")
                } else {
                    it.sendMessage("${ChatColor.LIGHT_PURPLE}The chat has been cleared by " + sender.name + ".")
                }
            }
        }

        Bukkit.getConsoleSender().sendMessage("${ChatColor.LIGHT_PURPLE}The chat has been cleared by " + sender.name + ".")
    }
}