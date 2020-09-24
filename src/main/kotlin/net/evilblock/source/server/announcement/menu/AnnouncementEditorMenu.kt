package net.evilblock.source.server.announcement.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.source.server.announcement.AnnouncementGroup
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

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (group in AnnouncementHandler.getGroups()) {
            buttons[buttons.size] = GroupButton(group)
        }

        return buttons
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[2] = AddGroupButton()

        for (i in 9..17) {
            buttons[i] = GlassButton(0)
        }

        return buttons
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 45
    }

    private inner class AddGroupButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Announcement Group"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "Create a new announcement group by following the setup procedure.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new chat filter")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .regex(EzPrompt.IDENTIFIER_REGEX)
                    .charLimit(16)
                    .promptText("${ChatColor.GREEN}Please input an ID for the announcement group.")
                    .acceptInput { _, input ->
                        if (AnnouncementHandler.getGroupById(input) != null) {
                            player.sendMessage("${ChatColor.RED}That ID is already taken!")
                            return@acceptInput
                        }

                        val group = AnnouncementGroup(input)

                        Tasks.async {
                            AnnouncementHandler.saveGroup(group)
                        }

                        GroupEditorMenu(group).openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class GroupButton(private val group: AnnouncementGroup) : Button() {
        override fun getName(player: Player): String {
            return if (AnnouncementHandler.getActiveGroup() == group) {
                "${ChatColor.GREEN}${ChatColor.BOLD}${group.id}"
            } else {
                "${ChatColor.RED}${ChatColor.BOLD}${group.id}"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}Announcements: ${ChatColor.YELLOW}${group.announcements.size}")
            description.add("${ChatColor.GRAY}Interval: ${ChatColor.YELLOW}${group.interval} ${TextUtil.pluralize(group.interval, "second", "seconds")}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit group")

            if (AnnouncementHandler.getActiveGroup() == group) {
                description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to make group inactive")
            } else {
                description.add("${ChatColor.YELLOW}${ChatColor.BOLD}MIDDLE-CLICK ${ChatColor.YELLOW}to make group active")
                description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete group")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.BOOKSHELF
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                GroupEditorMenu(group).openMenu(player)
            } else if (clickType.isRightClick) {
                if (AnnouncementHandler.getActiveGroup() == group) {
                    AnnouncementHandler.setActiveGroup(null)
                } else {
                    ConfirmMenu { confirmed ->
                        if (confirmed) {
                            AnnouncementHandler.deleteGroup(group)
                        }
                    }.openMenu(player)
                }
            } else if (clickType == ClickType.MIDDLE) {
                if (AnnouncementHandler.getActiveGroup() != group) {
                    AnnouncementHandler.setActiveGroup(group)
                }
            }
        }
    }

}