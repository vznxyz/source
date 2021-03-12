package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ClearCommand {

    @Command(
        names = ["clear", "ci", "clearinv"],
        description = "Clear a player's inventory",
        permission = "essentials.clear"
    )
    @JvmStatic
    fun clear(sender: CommandSender, @Param(name = "player", defaultValue = "self") target: Player) {
        if (sender != target && !sender.hasPermission("essentials.clear.other")) {
            sender.sendMessage("${ChatColor.RED}No permission to clear other player's inventories.")
            return
        }

        target.inventory.clear()
        target.inventory.armorContents = null

        if (sender != target) {
            sender.sendMessage(target.displayName + "${ChatColor.GOLD}'s inventory has been cleared.")
        } else {
            sender.sendMessage("${ChatColor.GOLD}Your inventory has been cleared.")
        }
    }
}