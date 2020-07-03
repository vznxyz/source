/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RenameCommand {

    private val customNameStarter: String = ChatColor.translateAlternateColorCodes('&', "&b&c&f")

    @Command(names = ["rename"], description = "Rename the item you're currently holding. Supports color codes", permission = "essentials.rename")
    @JvmStatic
    fun rename(sender: Player, @Param("name", wildcard = true) name: String) {
        var name = name
        if (sender.hasPermission("essentials.rename.color")) {
            name = ChatColor.translateAlternateColorCodes('&', name)
        }

        val item = sender.itemInHand
        if (item == null) {
            sender.sendMessage("${ChatColor.RED}You must be holding an item.")
            return
        }

        val isCustomEnchant = item.hasItemMeta() && item.itemMeta.hasDisplayName() && item.itemMeta.displayName.startsWith(
            customNameStarter
        )
        val meta = item.itemMeta
        meta.displayName = if (isCustomEnchant && !name.startsWith(customNameStarter)) customNameStarter + name else name
        item.itemMeta = meta

        sender.updateInventory()
        sender.sendMessage("${ChatColor.GOLD}Renamed the item in your hand to ${ChatColor.WHITE}$name${ChatColor.GOLD}.")
    }

}