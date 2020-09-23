package net.evilblock.source.chat.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.flag.Flag
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.source.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ClearChatCommand {

    @Command(
        names = ["clearchat", "cc", "source clear-chat", "source cc"],
        description = "Clears the chat",
        permission = Permissions.CLEAR_CHAT,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Flag(value = ["f", "force"], description = "Force chat to be cleared for all players", defaultValue = false) force: Boolean) {
        Tasks.async {
            Bukkit.getOnlinePlayers().forEach {
                if (!it.hasPermission(Permissions.CLEAR_CHAT_BYPASS) || force) {
                    for (i in 1..100) {
                        it.sendMessage("")
                    }
                }

                it.sendMessage("${ChatColor.LIGHT_PURPLE}The chat has been cleared by " + sender.name + ".")
            }
        }

        Bukkit.getConsoleSender().sendMessage("${ChatColor.LIGHT_PURPLE}The chat has been cleared by " + sender.name + ".")
    }
}