package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.flag.Flag
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ColorsCommand {

    @Command(
        names = ["print-colors"],
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Flag(value = ["g", "global"], description = "If colors should be broadcast") broadcast: Boolean) {
        if (broadcast) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aa&bb&cc&dd&ee&ff&00&11&22&33&44&55&66&77&88&99"))
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aa&bb&cc&dd&ee&ff&00&11&22&33&44&55&66&77&88&99"))
        }
    }

}