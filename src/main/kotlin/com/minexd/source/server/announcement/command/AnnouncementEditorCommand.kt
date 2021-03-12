package com.minexd.source.server.announcement.command

import net.evilblock.cubed.command.Command
import com.minexd.source.server.announcement.menu.AnnouncementEditorMenu
import com.minexd.source.util.Permissions
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