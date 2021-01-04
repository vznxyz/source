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
        try {
            Source.instance.reloadConfig()
            sender.sendMessage("${ChatColor.GREEN}Successfully reloaded Source/config.yml!")
        } catch (e: Exception) {
            e.printStackTrace()
            sender.sendMessage("${ChatColor.RED}Failed to reload Source/config.yml!")
        }

        try {
            Source.instance.loadServerConfig()
            sender.sendMessage("${ChatColor.GREEN}Successfully reloaded Source/server-config.json!")
        } catch (e: Exception) {
            e.printStackTrace()
            sender.sendMessage("${ChatColor.RED}Failed to reload Source/server-config.yml!")
        }
    }

}