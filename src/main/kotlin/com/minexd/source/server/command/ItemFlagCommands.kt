package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

object ItemFlagCommands {

    @Command(
        names = ["item-flag add", "itemflag add"],
        description = "Add an item flag to an item",
        permission = "op"
    )
    @JvmStatic
    fun add(player: Player, @Param(name = "flag") flag: ItemFlag) {
        if (player.inventory.itemInHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding an item!")
            return
        }

        val item = player.inventory.itemInHand
        item.itemMeta.addItemFlags(flag)

        player.updateInventory()
        player.sendMessage("${ChatColor.GREEN}Added the ${ChatColor.WHITE}${ChatColor.BOLD}${flag.name} ${ChatColor.GREEN}flag to the item!")
    }

    @Command(
        names = ["item-flag remove", "itemflag remove"],
        description = "Remove an item flag from an item",
        permission = "op"
    )
    @JvmStatic
    fun remove(player: Player, @Param(name = "flag") flag: ItemFlag) {
        if (player.inventory.itemInHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding an item!")
            return
        }

        val item = player.inventory.itemInHand
        item.itemMeta.removeItemFlags(flag)

        player.updateInventory()
        player.sendMessage("${ChatColor.GREEN}Removed the ${ChatColor.WHITE}${ChatColor.BOLD}${flag.name} ${ChatColor.GREEN}flag from the item!")
    }

    private val LEATHER_ARMOR = listOf(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS)

    @Command(
        names = ["color-armor"],
        description = "Colors leather armor",
        permission = "op"
    )
    @JvmStatic
    fun colorArmor(player: Player, @Param(name = "colorInt") colorValue: Int) {
        if (player.inventory.itemInHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding an item!")
            return
        }

        val item = player.inventory.itemInHand
        if (!LEATHER_ARMOR.contains(item.type)) {
            player.sendMessage("${ChatColor.RED}You must be holding a piece of leather armor!")
            return
        }

        ItemUtils.colorLeatherArmor(item, Color.fromRGB(colorValue))

        player.updateInventory()
        player.sendMessage("${ChatColor.GREEN}Armor has been colored!")
    }

    @Command(
        names = ["color-armor"],
        description = "Colors leather armor",
        permission = "op"
    )
    @JvmStatic
    fun colorArmorRGB(player: Player, @Param(name = "r") r: Int, @Param(name = "g") g: Int, @Param(name = "b") b: Int) {
        if (player.inventory.itemInHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding an item!")
            return
        }

        val item = player.inventory.itemInHand
        if (!LEATHER_ARMOR.contains(item.type)) {
            player.sendMessage("${ChatColor.RED}You must be holding a piece of leather armor!")
            return
        }

        ItemUtils.colorLeatherArmor(item, Color.fromRGB(r, g, b))

        player.updateInventory()
        player.sendMessage("${ChatColor.GREEN}Armor has been colored!")
    }

}