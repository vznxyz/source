package net.evilblock.source.server.announcement.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.source.server.announcement.Announcement
import net.evilblock.source.server.announcement.AnnouncementHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class EditAnnouncementLinesMenu(private val announcement: Announcement) : TextEditorMenu(lines = announcement.lines) {

    override fun getPromptBuilder(player: Player): EzPrompt.Builder {
        return EzPrompt.Builder()
            .promptText("${ChatColor.GREEN}Please input the new announcement text.")
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditAnnouncementMenu(announcement).openMenu(player)
        }
    }

    override fun onSave(player: Player, list: List<String>) {
        announcement.lines = lines.toMutableList()
        AnnouncementHandler.saveAnnouncement(announcement)
    }

}