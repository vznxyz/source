package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SudoCommands {

    @Command(
        names = ["sudo"],
        description = "Force a player to perform a command",
        permission = "essentials.sudo"
    )
    @JvmStatic
    fun sudo(sender: CommandSender, @Param(name = "player") target: Player, @Param(name = "command", wildcard = true) command: String) {
        target.chat("/$command")
        sender.sendMessage("${ChatColor.GOLD}Forced ${ChatColor.WHITE}${target.displayName}${ChatColor.GOLD} to run ${ChatColor.WHITE}'/$command'${ChatColor.GOLD}.")
    }

    @Command(
        names = ["sudoall"],
        description = "Force all players to perform a command",
        permission = "essentials.sudoall"
    )
    @JvmStatic
    fun sudoAll(sender: CommandSender, @Param(name = "command", wildcard = true) command: String) {
        Bukkit.getOnlinePlayers().forEach { it.chat("/$command") }
        sender.sendMessage("${ChatColor.GOLD}Forced all players to run ${ChatColor.WHITE}'/$command'${ChatColor.GOLD}.")
    }

    @Command(
        names = ["forcechat"],
        description = "Force a player to chat",
        permission = "essentials.forcechat"
    )
    @JvmStatic
    fun chat(sender: CommandSender, @Param(name = "player") target: Player, @Param(name = "message", wildcard = true) message: String) {
        target.chat(message)
        sender.sendMessage("${ChatColor.GOLD}Forced ${ChatColor.WHITE}${target.displayName}${ChatColor.GOLD} to chat ${ChatColor.WHITE}'$message'${ChatColor.GOLD}.")
    }

    @Command(
        names = ["forcechatall"],
        description = "Force all players to chat",
        permission = "essentials.forcechatall"
    )
    @JvmStatic
    fun chatAll(sender: CommandSender, @Param(name = "message", wildcard = true) message: String) {
        Bukkit.getOnlinePlayers().forEach { it.chat(message) }
        sender.sendMessage("${ChatColor.GOLD}Forced all players to chat ${ChatColor.WHITE}'$message'${ChatColor.GOLD}.")
    }
}