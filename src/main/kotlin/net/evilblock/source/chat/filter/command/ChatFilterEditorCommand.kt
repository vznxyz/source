package net.evilblock.source.chat.filter.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.chat.filter.menu.ChatFilterEditor
import net.evilblock.source.util.Permissions
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