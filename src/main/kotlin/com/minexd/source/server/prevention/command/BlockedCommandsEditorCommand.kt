package com.minexd.source.server.prevention.command

import net.evilblock.cubed.command.Command
import com.minexd.source.server.prevention.menu.EditBlockedCommandsMenu
import com.minexd.source.util.Permissions
import org.bukkit.entity.Player

object BlockedCommandsEditorCommand {

    @Command(
        names = ["source disallowed-cmds editor"],
        description = "Opens the Disallowed Commands editor",
        permission = Permissions.DISALLOWED_COMMANDS_EDITOR
    )
    @JvmStatic
    fun execute(player: Player) {
        EditBlockedCommandsMenu().openMenu(player)
    }

}