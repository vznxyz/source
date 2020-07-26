package net.evilblock.source.server.announcement.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.source.server.announcement.Announcement
import net.evilblock.source.server.announcement.AnnouncementHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class AnnouncementEditorMenu : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Announcements Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[2] = AddAnnouncementButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (announcement in AnnouncementHandler.announcements) {
            buttons[buttons.size] = AnnouncementButton(announcement)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    private inner class AddAnnouncementButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Announcement"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "Create a new announcement by following the setup procedure.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new announcement")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText("${ChatColor.GREEN}Please input the new announcement.")
                    .acceptInput { player, input ->
                        val announcement = Announcement(lines = arrayListOf(input))

                        AnnouncementHandler.announcements.add(announcement)
                        AnnouncementHandler.saveAnnouncement(announcement)

                        player.sendMessage("${ChatColor.GREEN}Successfully created a new announcement.")

                        EditAnnouncementMenu(announcement).openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class AnnouncementButton(private val announcement: Announcement) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Announcement #${announcement.order}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")

            for (line in announcement.lines) {
                description.add(ChatColor.translateAlternateColorCodes('&', line))
            }

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit this announcement")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete this announcement")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditAnnouncementMenu(announcement).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        AnnouncementHandler.announcements.remove(announcement)
                        AnnouncementHandler.deleteAnnouncement(announcement)
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made.")
                    }

                    this@AnnouncementEditorMenu.openMenu(player)
                }
            }
        }
    }

}