package net.evilblock.source.messaging.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.messaging.MessagingManager
import net.evilblock.source.messaging.event.ToggleMessagesEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ToggleMessagesCommand {

    @Command(
        names = ["togglepm", "tpm"],
        description = "Toggle private messaging",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val toggle = MessagingManager.toggleMessages(player.uniqueId, !MessagingManager.isMessagesDisabled(player.uniqueId))

        if (toggle) {
            player.sendMessage("${ChatColor.RED}Private messages have been disabled.")
        } else {
            player.sendMessage("${ChatColor.GREEN}Private messages have been enabled.")
        }

        val event = ToggleMessagesEvent(player.uniqueId, !toggle)
        Bukkit.getPluginManager().callEvent(event)
    }

}