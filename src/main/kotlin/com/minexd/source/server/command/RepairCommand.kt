package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

object RepairCommand {

    @Command(
        names = ["repair", "fix"],
        description = "Repair the item you're currently holding",
        permission = "essentials.repair"
    )
    @JvmStatic
    fun repair(sender: Player) {
        val item = sender.itemInHand
        if (item == null || item.type == Material.AIR) {
            sender.sendMessage("${ChatColor.RED}You must be holding an item!")
            return
        }

        if (!Enchantment.DURABILITY.canEnchantItem(item)) {
            sender.sendMessage("${ChatColor.RED}${ItemUtils.getName(item)} cannot be repaired!")
            return
        }

        if (item.durability == item.type.maxDurability) {
            sender.sendMessage("${ChatColor.RED}That ${ChatColor.WHITE}${ItemUtils.getName(item)}${ChatColor.RED} already has max durability!")
            return
        }

        item.durability = 0.toShort()
        sender.sendMessage("${ChatColor.GOLD}Your ${ChatColor.WHITE}${ItemUtils.getName(item)} ${ChatColor.GOLD}has been repaired!")
    }

}