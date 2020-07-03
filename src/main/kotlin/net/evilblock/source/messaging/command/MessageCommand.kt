package net.evilblock.source.messaging.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.source.messaging.MessagingManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MessageCommand {

    @Command(
        names = ["message", "msg", "m", "whisper", "w", "tell", "t"],
        description = "Send a player a private message",
        async = true
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "player") target: Player, @Param(name = "message", wildcard = true) message: String) {
        if (sender.uniqueId == target.uniqueId) {
            sender.sendMessage("${ChatColor.RED}You can't message yourself.")
            return
        }

        if (!MessagingManager.canMessage(sender, target)) {
            return
        }

        MessagingManager.sendMessage(sender, target, message)
    }

}