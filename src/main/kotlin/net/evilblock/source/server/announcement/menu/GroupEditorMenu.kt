package net.evilblock.source.server.announcement.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.source.server.announcement.Announcement
import net.evilblock.source.server.announcement.AnnouncementGroup
import net.evilblock.source.server.announcement.AnnouncementHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class GroupEditorMenu(private val group: AnnouncementGroup) : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Announcements Editor - ${group.id}"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = AddAnnouncementButton()
        buttons[2] = SetIntervalButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (announcement in group.announcements) {
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

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                AnnouncementEditorMenu().openMenu(player)
            }
        }
    }

    private inner class AddAnnouncementButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Announcement"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "Create a new announcement by following the setup procedure.", linePrefix = ChatColor.GRAY.toString()))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new announcement")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText("${ChatColor.GREEN}Please input the first line of the announcement.")
                    .acceptInput { _, input ->
                        Tasks.async {
                            val announcement = Announcement(lines = arrayListOf(input))
                            group.announcements.add(announcement)

                            AnnouncementHandler.saveGroup(group)

                            player.sendMessage("${ChatColor.GREEN}Successfully added a new announcement to the ${group.id} group.")

                            Tasks.sync {
                                EditAnnouncementMenu(group, announcement).openMenu(player)
                            }
                        }
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class SetIntervalButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Interval"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "The interval is the amount of time (in seconds) between each announcement broadcast.", linePrefix = ChatColor.GRAY.toString()))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit interval")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.WATCH
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                NumberPrompt(promptText = "${ChatColor.GREEN}Please input the new interval, in seconds.") { number ->
                    if (number.toInt() < 1) {
                        player.sendMessage("${ChatColor.RED}The interval must be at least 1 second!")
                        return@NumberPrompt
                    }

                    group.interval = number.toInt()

                    Tasks.async {
                        AnnouncementHandler.saveGroup(group)
                    }

                    this@GroupEditorMenu.openMenu(player)
                }.start(player)
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
                EditAnnouncementMenu(group, announcement).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        group.announcements.remove(announcement)

                        Tasks.async {
                            AnnouncementHandler.saveGroup(group)
                        }
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made.")
                    }

                    this@GroupEditorMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

}