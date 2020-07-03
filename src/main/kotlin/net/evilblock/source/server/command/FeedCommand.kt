package net.evilblock.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object FeedCommand {

    @Command(
        names = ["feed"],
        description = "Feed a player",
        permission = "essentials.feed"
    )
    @JvmStatic
    fun execute(sender: Player, @Param("player", "self") target: Player) {
        if (sender != target && !sender.hasPermission("essentials.feed.other")) {
            sender.sendMessage("${ChatColor.RED}No permission to feed other players.")
            return
        }

        target.foodLevel = 20
        target.saturation = 10.0f

        if (sender != target) {
            sender.sendMessage(target.displayName + ChatColor.GOLD + " has been fed.")
        }

        target.sendMessage("${ChatColor.GOLD}You have been fed.")
    }

}