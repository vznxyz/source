package com.minexd.source.server.prevention.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import com.minexd.source.server.prevention.BlockedCommandsHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class EditBlockedCommandsMenu : TextEditorMenu(lines = BlockedCommandsHandler.blockedCommands) {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Disallowed Commands"
    }

    override fun getPromptBuilder(player: Player, index: Int): EzPrompt.Builder {
        return EzPrompt.Builder()
            .promptText("${ChatColor.GREEN}Please input the new disallowed command.")
    }

    override fun onClose(player: Player) {

    }

    override fun onSave(player: Player, list: List<String>) {
        BlockedCommandsHandler.blockedCommands = list.toMutableList().map {
            return@map if (it.startsWith("\\")) {
                it.replaceFirst("\\", "")
            } else {
                it
            }
        }.toMutableList()

        BlockedCommandsHandler.saveToRedis()
    }

}