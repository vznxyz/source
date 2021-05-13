package com.minexd.source.server.command

import com.minexd.source.Source
import com.minexd.source.server.menu.TrashMenu
import net.evilblock.cubed.command.Command
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object TrashCommand {

    @Command(
        names = ["trash", "trashcan", "disposal", "dispose"],
        description = "",
        permission = "essentials.trash"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (Source.instance.config.getBoolean("trash-disabled", false)) {
            player.sendMessage("${ChatColor.RED}The trash can is currently disabled!")
            return
        }

        TrashMenu().openMenu(player)
    }

}