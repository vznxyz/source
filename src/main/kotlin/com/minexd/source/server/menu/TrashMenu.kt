package com.minexd.source.server.menu

import com.minexd.source.Source
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TrashMenu : Menu() {

    private val insertedItems = arrayListOf<ItemStack>()

    override fun getTitle(player: Player): String {
        return "Trash"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return emptyMap()
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 36
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (Source.instance.config.getBoolean("trash-disabled", false)) {
            return
        }

        val details = insertedItems.map { "${ChatColor.GRAY}${Constants.DOT_SYMBOL} ${ChatColor.RESET}${ItemUtils.getDisplayName(it)}" }

        Tasks.delayed(1L) {
            ConfirmMenu("Permanently trash items?", details) { confirmed ->
                if (confirmed) {
                    player.sendMessage("${ChatColor.YELLOW}Your items have been permanently trashed!")
                } else {
                    for (itemStack in insertedItems) {
                        player.inventory.addItem(itemStack)
                    }

                    player.updateInventory()
                    player.sendMessage("${ChatColor.RED}Your items have been returned to your inventory!")
                }
            }.openMenu(player)
        }
    }

    override fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean {
        insertedItems.add(itemStack)
        return true
    }

    override fun acceptsInsertedItem(player: Player, itemStack: ItemStack, slot: Int): Boolean {
        insertedItems.add(itemStack)
        return true
    }

    override fun acceptsDraggedItems(player: Player, items: Map<Int, ItemStack>): Boolean {
        for (itemStack in items.values) {
            insertedItems.add(itemStack)
        }
        return true
    }

}