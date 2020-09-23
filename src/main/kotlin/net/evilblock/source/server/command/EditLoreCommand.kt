package net.evilblock.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.server.menu.EditLoreMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EditLoreCommand {

    @Command(
        names = ["lore", "editlore"],
        description = "Edit your lore using a text editor",
        permission = "op"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (player.inventory.itemInMainHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding an item.")
            return
        }

        EditLoreMenu(player.inventory.itemInMainHand).openMenu(player)
    }

}