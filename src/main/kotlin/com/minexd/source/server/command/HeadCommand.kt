package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object HeadCommand {

    @Command(
        names = ["head"],
        description = "Spawn yourself a player's head",
        permission = "essentials.head"
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "name", defaultValue = "self") name: String) {
        var name = name
        if (name == "self") {
            name = sender.name
        }

        val item = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())
        val meta = item.itemMeta as SkullMeta
        meta.owner = name
        item.itemMeta = meta
        sender.inventory.addItem(item)
        sender.sendMessage("${ChatColor.GOLD}You were given ${ChatColor.WHITE}$name${ChatColor.GOLD}'s head.")
    }

}