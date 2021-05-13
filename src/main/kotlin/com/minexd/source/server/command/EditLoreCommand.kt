package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import com.minexd.source.server.menu.EditLoreMenu
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EditLoreCommand {

    @Command(
        names = ["lore", "editlore", "edit-lore"],
        description = "Edit your lore using a text editor",
        permission = "op"
    )
    @JvmStatic
    fun editLore(player: Player) {
        if (player.inventory.itemInHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding an item!")
            return
        }

        EditLoreMenu(player.inventory.itemInHand).openMenu(player)
    }

    @Command(
        names = ["copylore", "copy-lore"],
        description = "Copy a lore of an item onto another item",
        permission = "op"
    )
    @JvmStatic
    fun copyLore(player: Player) {
        if (player.inventory.itemInHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding an item!")
            return
        }

        SelectItemStackMenu { itemStack ->
            val item = player.inventory.itemInHand
            val meta = item.itemMeta
            meta.lore = itemStack.itemMeta.lore
            item.itemMeta = meta
            player.updateInventory()
            player.sendMessage("${ChatColor.GREEN}Copied lore!")
        }.openMenu(player)
    }

}