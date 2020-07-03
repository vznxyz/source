package net.evilblock.source.messaging.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.source.messaging.MessagingManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object IgnoreCommand {

    @Command(
        names = ["ignore", "block"],
        description = "Start ignoring a player. You won't receive private messages from them or see their public chat messages",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param("player") target: UUID) {
        if (player.uniqueId == target) {
            player.sendMessage("${ChatColor.RED}You can't ignore yourself.")
            return
        }

        val targetUsername = Cubed.instance.uuidCache.name(target)

        if (MessagingManager.isIgnored(player.uniqueId, target)) {
            player.sendMessage("${ChatColor.RED}You are already ignoring ${ChatColor.WHITE}${targetUsername}${ChatColor.RED}.")
            return
        }

        MessagingManager.addToIgnoreList(player.uniqueId, target)

        player.sendMessage("${ChatColor.YELLOW}Now ignoring ${ChatColor.WHITE}${targetUsername}${ChatColor.YELLOW}.")
    }

}