package net.evilblock.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.Source
import net.evilblock.source.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ReloadCommand {

    @Command(
        names = ["source reload"],
        description = "Reloads the Source configuration",
        permission = Permissions.RELOAD
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        Source.instance.reloadConfig()
        sender.sendMessage("${ChatColor.GREEN}Reloaded the Source configuration!")
    }

}