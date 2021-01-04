package net.evilblock.source.chat.filter.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.source.chat.filter.ChatFilter
import net.evilblock.source.chat.filter.ChatFilterHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class ChatFilterEditor : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Chat Filters"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = AddChatFilterButton()

        for (i in 9..17) {
            buttons[i] = GlassButton(0)
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (filter in ChatFilterHandler.getFilters()) {
            buttons[buttons.size] = ChatFilterButton(filter)
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
        return 54
    }

    private inner class AddChatFilterButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Chat Filter"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(TextSplitter.split(
                text = "Create a new chat filter by following the setup procedure.",
                linePrefix = "${ChatColor.GRAY}")
            )

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new chat filter")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText("${ChatColor.GREEN}Please input the new chat filter regex pattern.")
                    .acceptInput { inputPattern ->
                        Tasks.delayed(1L) {
                            EzPrompt.Builder()
                                .promptText("${ChatColor.GREEN}Please input the chat filter's description.")
                                .acceptInput { inputDescription ->
                                    val chatFilter = ChatFilter(description = inputDescription, regex = inputPattern)
                                    ChatFilterHandler.trackFilter(chatFilter)

                                    Tasks.async {
                                        ChatFilterHandler.saveFilter(chatFilter)
                                    }

                                    this@ChatFilterEditor.openMenu(player)
                                }
                                .build()
                                .start(player)
                        }
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class ChatFilterButton(private val chatFilter: ChatFilter) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${chatFilter.description}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}Pattern: ${chatFilter.pattern.pattern()}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit pattern")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete filter")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText("${ChatColor.GREEN}Please input the new filter pattern.")
                    .acceptInput { input ->
                        chatFilter.pattern = input.toRegex().toPattern()

                        Tasks.async {
                            ChatFilterHandler.saveFilter(chatFilter)
                        }

                        this@ChatFilterEditor.openMenu(player)
                    }
                    .build()
                    .start(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        Tasks.async {
                            ChatFilterHandler.deleteFilter(chatFilter)
                        }
                    }

                    this@ChatFilterEditor.openMenu(player)
                }.openMenu(player)
            }
        }
    }

}