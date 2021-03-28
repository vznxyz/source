package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

object MoreCommand {

    @Command(
        names = ["more", "more-items"],
        description = "Gives you more of the item in your hand",
        permission = "essentials.more"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (player.inventory.itemInHand == null || player.inventory.itemInHand.type == Material.AIR) {
            player.sendMessage("${ChatColor.RED}You don't have anything in your hand!")
            return
        }

        player.inventory.itemInHand.amount = player.inventory.itemInHand.maxStackSize
        player.updateInventory()
    }

}