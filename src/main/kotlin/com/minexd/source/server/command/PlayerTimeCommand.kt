package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.flag.Flag
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object PlayerTimeCommand {

    @Command(
        names = ["ptime", "player-time"],
        description = "Opens a crafting table",
        permission = "essentials.time.self"
    )
    @JvmStatic
    fun rename(sender: Player, @Flag(value = ["f", "fixed"]) fixed: Boolean, @Param(name = "time") time: Long) {
        sender.setPlayerTime(time, !fixed)
        sender.sendMessage("${ChatColor.GREEN}Your client time has been updated!")
    }

}