package net.evilblock.source.server.announcement.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.source.server.announcement.AnnouncementHandler
import net.evilblock.source.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object AnnouncementIntervalCommand {

    @Command(
        names = ["source announcement interval"],
        description = "Set the announcement interval",
        permission = Permissions.ANNOUNCEMENT_EDITOR
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "seconds") seconds: Int) {
        AnnouncementHandler.setInterval(seconds)
        AnnouncementHandler.startTask()

        sender.sendMessage("${ChatColor.GREEN}Set announcement interval to $seconds seconds!")
    }

}