/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.def

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object BroadcastCommand {

    @Command(["bc", "broadcast"], "essentials.broadcast")
    @JvmStatic
    fun execute(sender: CommandSender, @Param("message", wildcard = true) message: String) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message))
    }

}