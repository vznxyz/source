package com.minexd.source.server.announcement.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import com.minexd.source.server.announcement.Announcement
import com.minexd.source.server.announcement.AnnouncementGroup
import com.minexd.source.server.announcement.AnnouncementHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditAnnouncementMenu(private val group: AnnouncementGroup, private val announcement: Announcement) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Announcement"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = EditLinesButton()
        buttons[1] = EditOrderButton()

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                GroupEditorMenu(group).openMenu(player)
            }
        }
    }

    private inner class EditLinesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Lines"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The lines broadcast to all players",
                "${ChatColor.GRAY}when sending this announcement.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit lines"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.SIGN
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditAnnouncementLinesMenu(
                    group,
                    announcement
                ).openMenu(player)
            }
        }
    }

    private inner class EditOrderButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Order ${ChatColor.GRAY}(${announcement.order})"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The order in which announcements",
                "${ChatColor.GRAY}are broadcast.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to increase order by +1",
                "${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to decrease order by -1",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.GREEN}to increase order by +10",
                "${ChatColor.RED}${ChatColor.BOLD}SHIFT RIGHT-CLICK ${ChatColor.RED}to decrease order by -10"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.LEVER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val mod = if (clickType.isShiftClick) 10 else 1

            val update = when {
                clickType.isLeftClick -> {
                    announcement.order += mod
                    true
                }
                clickType.isRightClick -> {
                    announcement.order = 0.coerceAtLeast(announcement.order - mod)
                    true
                }
                else -> {
                    false
                }
            }

            if (update) {
                Tasks.async {
                    AnnouncementHandler.saveGroup(group)
                }
            }
        }
    }

}