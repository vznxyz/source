package net.evilblock.source.server.announcement.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.server.announcement.menu.AnnouncementEditorMenu
import net.evilblock.source.util.Permissions
import org.bukkit.entity.Player

object AnnouncementEditorCommand {

    @Command(
        names = ["source announcement editor"],
        description = "Opens the Announcement editor",
        permission = Permissions.ANNOUNCEMENT_EDITOR
    )
    @JvmStatic
    fun execute(player: Player) {
        AnnouncementEditorMenu().openMenu(player)
    }

}