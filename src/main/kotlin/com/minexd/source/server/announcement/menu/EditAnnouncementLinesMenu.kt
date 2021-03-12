package com.minexd.source.server.announcement.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.text.TextUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import com.minexd.source.server.announcement.Announcement
import com.minexd.source.server.announcement.AnnouncementGroup
import com.minexd.source.server.announcement.AnnouncementHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class EditAnnouncementLinesMenu(private val group: AnnouncementGroup, private val announcement: Announcement) : TextEditorMenu(lines = announcement.lines) {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Announcement Lines"
    }

    override fun getPromptBuilder(player: Player, index: Int): EzPrompt.Builder {
        return EzPrompt.Builder()
            .promptText("${ChatColor.GREEN}Please input the new announcement text.")
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditAnnouncementMenu(group, announcement).openMenu(player)
        }
    }

    override fun onSave(player: Player, list: List<String>) {
        announcement.lines = list.map { line ->
            if (TextUtil.isBlank(text = line)) {
                ""
            } else {
                line
            }
        }.toMutableList()

        Tasks.async {
            AnnouncementHandler.saveGroup(group)
        }
    }

}