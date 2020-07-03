package net.evilblock.source.messaging.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.messaging.MessagingManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object IgnoreListClearCommand {

    @Command(
        names = ["ignore clear"],
        description = "Clear your ignore list",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        MessagingManager.clearIgnoreList(player.uniqueId)
        player.sendMessage("${ChatColor.YELLOW}You've cleared your ignore list.")
    }

}