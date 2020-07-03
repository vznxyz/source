package net.evilblock.source.messaging.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.source.messaging.MessagingManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ReplyCommand {

    @Command(
        names = ["reply", "r"],
        description = "Reply to the player you're in a conversation with",
        async = true
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "message", defaultValue = "   ", wildcard = true) message: String) {
        val lastMessaged = MessagingManager.getLastMessaged(sender.uniqueId)

        if (message == "   ") {
            if (lastMessaged == null) {
                sender.sendMessage("${ChatColor.RED}You aren't in a conversation.")
            } else {
                sender.sendMessage("${ChatColor.GOLD}You are in a conversation with ${ChatColor.WHITE}${Cubed.instance.uuidCache.name(lastMessaged)}${ChatColor.GOLD}.")
            }

            return
        }

        if (lastMessaged == null) {
            sender.sendMessage("${ChatColor.RED}You have no one to reply to.")
            return
        }

        val target = Bukkit.getPlayer(lastMessaged)

        if (target == null) {
            sender.sendMessage("${ChatColor.RED}That player has logged out.")
            return
        }

        if (!MessagingManager.canMessage(sender, target)) {
            return
        }

        MessagingManager.sendMessage(sender, target, message)
    }

}