package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import com.minexd.source.server.menu.EditLoreMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EditLoreCommand {

    @Command(
        names = ["lore", "editlore", "edit-lore"],
        description = "Edit your lore using a text editor",
        permission = "op"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (player.inventory.itemInHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding an item.")
            return
        }

        EditLoreMenu(player.inventory.itemInHand).openMenu(player)
    }

}