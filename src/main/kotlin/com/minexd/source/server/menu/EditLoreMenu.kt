package com.minexd.source.server.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.text.TextUtil
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class EditLoreMenu(private val itemStack: ItemStack) : TextEditorMenu(lines = if (itemStack.hasItemMeta() && itemStack.itemMeta.hasLore()) itemStack.itemMeta.lore else emptyList()) {

    init {
        supportsColors = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Lore"
    }

    override fun onSave(player: Player, list: List<String>) {
        val lines = list.map { line ->
            if (TextUtil.isBlank(text = line)) {
                ""
            } else {
                line
            }
        }

        val meta = itemStack.itemMeta
        meta.lore = lines

        itemStack.itemMeta = meta

        player.sendMessage("${ChatColor.GOLD}Updated lore of item in your hand!")
    }

    override fun onClose(player: Player) {

    }

}