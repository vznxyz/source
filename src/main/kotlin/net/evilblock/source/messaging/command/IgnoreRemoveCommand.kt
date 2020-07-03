package net.evilblock.source.messaging.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.source.messaging.MessagingManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object IgnoreRemoveCommand {

    @Command(
        names = ["ignore remove", "unignore"],
        description = "Stop ignoring a player",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param("player") target: UUID) {
        val targetUsername = Cubed.instance.uuidCache.name(target)

        if (!MessagingManager.isIgnored(player.uniqueId, target)) {
            player.sendMessage("${ChatColor.RED}You aren't ignoring ${ChatColor.WHITE}${targetUsername}${ChatColor.RED}.")
            return
        }

        MessagingManager.removeFromIgnoreList(player.uniqueId, target)

        player.sendMessage("${ChatColor.YELLOW}You are no longer ignoring ${ChatColor.WHITE}${targetUsername}${ChatColor.YELLOW}.")
    }

}