package com.minexd.source.chat.filter.command

import net.evilblock.cubed.command.Command
import com.minexd.source.chat.filter.menu.ChatFilterEditor
import com.minexd.source.util.Permissions
import org.bukkit.entity.Player

object ChatFilterEditorCommand {

    @Command(
        names = ["source chat-filter"],
        description = "Opens the chat-filter editor",
        permission = Permissions.CLEAR_CHAT
    )
    @JvmStatic
    fun execute(player: Player) {
        ChatFilterEditor().openMenu(player)
    }

}