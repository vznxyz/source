package net.evilblock.source.messaging.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.messaging.MessagingManager
import net.evilblock.source.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpyCommand {

    @Command(
        names = ["spy"],
        description = "Toggle global private message spying",
        permission = Permissions.MESSAGE_SPY,
        async = true
    )
    @JvmStatic
    fun execute(sender: Player) {
        val contains = MessagingManager.globalSpy.contains(sender.uniqueId)

        if (contains) {
            MessagingManager.globalSpy.remove(sender.uniqueId)
        } else {
            MessagingManager.globalSpy.add(sender.uniqueId)
        }

        sender.sendMessage("${ChatColor.GOLD}Global chat spy has been set to ${ChatColor.WHITE}${!contains}${ChatColor.GOLD}.")
    }

}