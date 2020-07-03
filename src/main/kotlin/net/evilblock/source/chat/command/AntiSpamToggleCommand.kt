package net.evilblock.source.chat.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.chat.spam.ChatSpamListeners
import net.evilblock.source.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object AntiSpamToggleCommand {

    @Command(
        names = ["anti-spam toggle"],
        description = "Toggle the anti-spam system",
        permission = Permissions.ANTI_SPAM_TOGGLE,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        ChatSpamListeners.SPAM_PREVENTION_ENABLED = !ChatSpamListeners.SPAM_PREVENTION_ENABLED

        if (ChatSpamListeners.SPAM_PREVENTION_ENABLED) {
            sender.sendMessage("${ChatColor.GREEN}Enabled chat anti-spam")
        } else {
            sender.sendMessage("${ChatColor.RED}Disabled chat anti-spam.")
        }
    }

}