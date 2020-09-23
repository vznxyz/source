package net.evilblock.source.messaging.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.messaging.MessagingManager
import net.evilblock.source.messaging.event.ToggleSoundsEvent
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ToggleSoundsCommand {

    @Command(
        names = ["sounds", "togglesounds"],
        description = "Toggle messaging sounds",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val toggle = !MessagingManager.isSoundsDisabled(player.uniqueId)

        MessagingManager.toggleSounds(player.uniqueId, toggle)

        if (toggle) {
            player.sendMessage("${ChatColor.YELLOW}Messaging sounds have been disabled.")
        } else {
            player.sendMessage("${ChatColor.YELLOW}Messaging sounds have been enabled.")
        }

        ToggleSoundsEvent(player.uniqueId, !toggle).call()
    }

}