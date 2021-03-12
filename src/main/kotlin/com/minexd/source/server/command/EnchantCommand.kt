/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.flag.Flag
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.enchantment.EnchantmentWrapper
import org.bukkit.ChatColor
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

object EnchantCommand {

    @Command(
        names = ["venchant", "mcenchant", "source enchant"],
        permission = "essentials.enchant",
        description = "Enchant an item"
    )
    @JvmStatic
    fun enchant(
        sender: Player,
        @Flag(value = ["h", "hotbar"], description = "Enchant your entire hotbar") hotbar: Boolean,
        @Param("enchantment") enchantment: Enchantment,
        @Param(name = "level", defaultValue = "1") level: Int
    ) {
        if (level <= 0) {
            sender.sendMessage("${ChatColor.RED}The level must be greater than 0.")
            return
        }

        if (!hotbar) {
            val item = sender.itemInHand
            if (item == null) {
                sender.sendMessage("${ChatColor.RED}You must be holding an item.")
                return
            }

            val wrapper = EnchantmentWrapper.parse(enchantment)
            if (level > wrapper.maxLevel) {
                if (!sender.hasPermission("essentials.enchant.force")) {
                    sender.sendMessage("${ChatColor.RED}The maximum enchanting level for " + wrapper.friendlyName + " is " + level + ". You provided " + level + ".")
                    return
                }

                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING: " + ChatColor.YELLOW + "You added " + wrapper.friendlyName + " " + level + " to this item. The default maximum value is " + wrapper.maxLevel + ".")
            }

            wrapper.enchant(item, level)

            sender.updateInventory()
            sender.sendMessage("${ChatColor.GOLD}Enchanted your " + ChatColor.WHITE + ItemUtils.getName(item) + ChatColor.GOLD + " with " + ChatColor.WHITE + wrapper.friendlyName + ChatColor.GOLD + " level " + ChatColor.WHITE + level + ChatColor.GOLD + ".")
        } else {
            val wrapper2 = EnchantmentWrapper.parse(enchantment)
            if (level > wrapper2.maxLevel && !sender.hasPermission("essentials.enchant.force")) {
                sender.sendMessage("${ChatColor.RED}The maximum enchanting level for " + wrapper2.friendlyName + " is " + level + ". You provided " + level + ".")
                return
            }

            var enchanted = 0
            for (slot in 0..8) {
                val item2 = sender.inventory.getItem(slot)
                if (item2 != null) {
                    if (wrapper2.canEnchantItem(item2)) {
                        wrapper2.enchant(item2, level)
                        ++enchanted
                    }
                }
            }

            if (enchanted == 0) {
                sender.sendMessage("${ChatColor.RED}No items in your hotbar can be enchanted with " + wrapper2.friendlyName + ".")
                return
            }

            if (level > wrapper2.maxLevel) {
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING: " + ChatColor.YELLOW + "You added " + wrapper2.friendlyName + " " + level + " to these items. The default maximum value is " + wrapper2.maxLevel + ".")
            }

            sender.sendMessage("${ChatColor.GOLD}Enchanted " + ChatColor.WHITE + enchanted + ChatColor.GOLD + " items with " + ChatColor.WHITE + wrapper2.friendlyName + ChatColor.GOLD + " level " + ChatColor.WHITE + level + ChatColor.GOLD + ".")
            sender.updateInventory()
        }
    }

}