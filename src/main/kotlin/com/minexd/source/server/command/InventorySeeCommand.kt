package com.minexd.source.server.command

import com.minexd.source.server.inventory.TrackedPlayerInventory
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.player.OfflinePlayerWrapper
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object InventorySeeCommand {

    @Command(
        names = ["invsee"],
        description = "Open a player's inventory",
        permission = "essentials.invsee"
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "player") wrapper: OfflinePlayerWrapper) {
        wrapper.loadAsync { player ->
            if (player == null) {
                sender.sendMessage("${ChatColor.RED}No online or offline player with the name ${wrapper.name} found.")
                return@loadAsync
            }

            if (player == sender) {
                sender.sendMessage("${ChatColor.RED}You can't invsee yourself!")
                return@loadAsync
            }

            sender.openInventory(TrackedPlayerInventory.get(player).getBukkitInventory())
            TrackedPlayerInventory.open.add(sender.uniqueId)
        }
    }

}